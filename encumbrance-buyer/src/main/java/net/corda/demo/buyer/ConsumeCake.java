package net.corda.demo.buyer;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.FinalityFlow;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;
import net.corda.demo.common.helper.FlowHelper;
import net.corda.demo.sc.contract.CakeContract;
import net.corda.demo.sc.state.Cake;
import net.corda.demo.sc.state.Expiry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.stream.Collectors;

@StartableByRPC
public class ConsumeCake extends FlowLogic<SignedTransaction> {
    private static final Logger logger = LoggerFactory.getLogger(ConsumeCake.class);
    private final ProgressTracker.Step GENERATING_TRANSACTION = new ProgressTracker.Step("Generating transaction to consume the cake.");
    private final ProgressTracker.Step VERIFYING_TRANSACTION = new ProgressTracker.Step("Verifying contract constraints of the cake.");
    private final ProgressTracker.Step SIGNING_TRANSACTION = new ProgressTracker.Step("Signing cake transaction with our private key.");
    private final ProgressTracker.Step FINALISING_TRANSACTION = new ProgressTracker.Step("Obtaining notary signature and consuming the scrumptious cake.") {
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

    private final String cakeId;

    public ConsumeCake(String cakeId) {
        this.cakeId = cakeId;
    }

    @Override
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {
        try {
            progressTracker.setCurrentStep(GENERATING_TRANSACTION);
            StateAndRef<Cake> cake = FlowHelper.getCakeById(cakeId, getServiceHub());
            Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
            StateAndRef<Expiry> expiryOfCake = FlowHelper.getExpiryOfCake(cakeId, getServiceHub());
            TransactionBuilder txBuilder = new TransactionBuilder(notary)
            .addInputState(cake)
            .addInputState(expiryOfCake)
            .addCommand(new CakeContract.Commands.Consume(), cake.getState().getData().getParticipants().stream().map(AbstractParty::getOwningKey).collect(Collectors.toList()))
            .setTimeWindow(Instant.now(), Duration.ofSeconds(10));

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
