package net.corda.demo.bakery;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;
import net.corda.core.utilities.ProgressTracker.Step;
import net.corda.demo.common.helper.FlowHelper;
import net.corda.demo.sc.contract.CakeContract;
import net.corda.demo.sc.contract.ExpiryContract;
import net.corda.demo.sc.state.Cake;
import net.corda.demo.sc.state.Expiry;
import net.corda.demo.sc.state.Flavour;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@StartableByRPC
public class CreateCake extends FlowLogic<SignedTransaction> {
    private static final Logger logger = LoggerFactory.getLogger(CreateCake.class);
    private final Step GENERATING_TRANSACTION = new Step("Generating transaction to create new flavoured cake.");
    private final Step VERIFYING_TRANSACTION = new Step("Verifying contract constraints of the cake.");
    private final Step SIGNING_TRANSACTION = new Step("Signing cake transaction with our private key.");
    private final Step FINALISING_TRANSACTION = new Step("Obtaining notary signature and completing cake creation.") {
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
            FINALISING_TRANSACTION
    );
    private String flavour;
    private String cakeId;
    private long expiryAfterMinute;

    public CreateCake(String flavour, String cakeId, long expireAfterMinutes) {
        this.flavour = flavour;
        this.cakeId = cakeId;
        if (expireAfterMinutes <= 0)
            throw new IllegalArgumentException("please provide positive value for expireAfterMinutes");
        this.expiryAfterMinute = expireAfterMinutes;
    }

    @Override
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {
        try {
            Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
            FlowHelper.checkIfCakeExists(cakeId, getServiceHub());
            progressTracker.setCurrentStep(GENERATING_TRANSACTION);
            Cake cake = new Cake(Flavour.fromText(flavour), getOurIdentity(), cakeId);
            Expiry expiry = new Expiry(Instant.now().plus(expiryAfterMinute, ChronoUnit.MINUTES), cakeId, cake.getOwner());
            TransactionBuilder txBuilder = new TransactionBuilder(notary)
                    .addOutputState(cake, CakeContract.CAKE_CONTRACT_ID, notary, 1)
                    .addOutputState(expiry, ExpiryContract.EXPIRY_CONTRACT_ID)
                    .addCommand(new CakeContract.Commands.Create(), cake.getOwner().getOwningKey())
                    .addCommand(new ExpiryContract.Commands.Create(), expiry.getOwner().getOwningKey())
                    .setTimeWindow(Instant.now(), Duration.ofSeconds(1));
            progressTracker.setCurrentStep(VERIFYING_TRANSACTION);
            txBuilder.verify(getServiceHub());
            progressTracker.setCurrentStep(SIGNING_TRANSACTION);
            SignedTransaction signedTransaction = getServiceHub().signInitialTransaction(txBuilder);
            progressTracker.setCurrentStep(FINALISING_TRANSACTION);
            return subFlow(new FinalityFlow(signedTransaction));
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new FlowException(e.getMessage());
        }
    }

}