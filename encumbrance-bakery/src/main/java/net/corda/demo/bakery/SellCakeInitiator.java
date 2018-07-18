package net.corda.demo.bakery;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.contracts.CommandAndState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;
import net.corda.demo.common.flow.SellCake;
import net.corda.demo.common.helper.FlowHelper;
import net.corda.demo.sc.contract.CakeContract;
import net.corda.demo.sc.contract.ExpiryContract;
import net.corda.demo.sc.state.Cake;
import net.corda.demo.sc.state.Expiry;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

@StartableByRPC
public class SellCakeInitiator extends SellCake {
    private final ProgressTracker.Step GENERATING_TRANSACTION = new ProgressTracker.Step("Generating transaction to sell the cake.");
    private final ProgressTracker.Step VERIFYING_TRANSACTION = new ProgressTracker.Step("Verifying contract constraints of the cake .");
    private final ProgressTracker.Step SIGNING_TRANSACTION = new ProgressTracker.Step("Signing cake transaction with our private key.");
    private final ProgressTracker.Step GATHERING_SIGS = new ProgressTracker.Step("Gathering the counterparty's signature.") {
        @Override
        public ProgressTracker childProgressTracker() {
            return CollectSignaturesFlow.Companion.tracker();
        }
    };
    private final ProgressTracker.Step FINALISING_TRANSACTION = new ProgressTracker.Step("Obtaining notary signature and selling cake.") {
        @Override
        public ProgressTracker childProgressTracker() {
            return FinalityFlow.Companion.tracker();
        }
    };
    // The progress tracker checkpoints each stage of the flow and outputs the specified messages when each
    // checkpoint is reached in the code. See the 'progressTracker.currentStep' expressions within the call()
    // function.
    private final ProgressTracker progressTracker = new ProgressTracker(
            GENERATING_TRANSACTION,
            VERIFYING_TRANSACTION,
            SIGNING_TRANSACTION,
            GATHERING_SIGS,
            FINALISING_TRANSACTION
    );

    private final String cakeId;
    private final String buyer;
    private final boolean includeEncumbrance;

    public SellCakeInitiator(String cakeId, String buyer, boolean includeEncumbrance) {
        this.cakeId = cakeId;
        this.buyer = buyer;
        this.includeEncumbrance = includeEncumbrance;
    }

    @Override
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {
        try {
            Set<Party> buyerSet = getServiceHub().getIdentityService().partiesFromName(buyer, true);
            if (buyerSet.isEmpty()) {
                throw new IllegalArgumentException("Illegal Buyer specified!");
            }
            Party buyerParty = buyerSet.iterator().next();
            if (buyerParty.equals(getOurIdentity()) || buyerParty.getName().getOrganisation().equalsIgnoreCase("Bakery")) {
                throw new IllegalArgumentException("Illegal Buyer specified!");
            }

            progressTracker.setCurrentStep(GENERATING_TRANSACTION);
            StateAndRef<Cake> cake = FlowHelper.getCakeById(cakeId, getServiceHub());
            Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
            CommandAndState commandAndState = cake.getState().getData().withNewOwner(buyerParty);
            StateAndRef<Expiry> expiryOfCake = FlowHelper.getExpiryOfCake(cakeId, getServiceHub());
            CommandAndState expiryStateCommand = expiryOfCake.getState().getData().withNewOwner(buyerParty);
            TransactionBuilder txBuilder = new TransactionBuilder(notary)
                    .addInputState(cake)
                    .addOutputState(commandAndState.getOwnableState(), CakeContract.CAKE_CONTRACT_ID, notary, 1) // Encumbrance is at index 1 
                    .addOutputState(expiryStateCommand.getOwnableState(), ExpiryContract.EXPIRY_CONTRACT_ID)
                    .addCommand(commandAndState.getCommand(), Arrays.asList(commandAndState.getOwnableState().getOwner().getOwningKey(), cake.getState().getData().getOwner().getOwningKey()))
                    .addCommand(expiryStateCommand.getCommand(), Arrays.asList(expiryStateCommand.getOwnableState().getOwner().getOwningKey(), expiryOfCake.getState().getData().getOwner().getOwningKey()))
                    .setTimeWindow(Instant.now(), Duration.ofSeconds(10));

            if (includeEncumbrance) {
                txBuilder.addInputState(expiryOfCake);
            }

            progressTracker.setCurrentStep(VERIFYING_TRANSACTION);
            txBuilder.verify(getServiceHub());

            progressTracker.setCurrentStep(SIGNING_TRANSACTION);
            SignedTransaction partiallySignedTx = getServiceHub().signInitialTransaction(txBuilder);
            FlowSession buyerSession = initiateFlow(buyerParty);

            progressTracker.setCurrentStep(GATHERING_SIGS);
            final SignedTransaction fullySignedTx = subFlow(
                    new CollectSignaturesFlow(partiallySignedTx, Collections.singletonList(buyerSession)));

            progressTracker.setCurrentStep(FINALISING_TRANSACTION);
            return subFlow(new FinalityFlow(fullySignedTx));
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new FlowException(e.getMessage());
        }
    }
}
