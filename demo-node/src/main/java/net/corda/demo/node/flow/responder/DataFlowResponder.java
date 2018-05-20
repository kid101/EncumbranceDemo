package net.corda.demo.node.flow.responder;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.demo.node.flow.DataFlow;
import net.corda.demo.node.flow.initiator.DataFlowInitiator;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowSession;
import net.corda.core.flows.InitiatedBy;
import net.corda.core.flows.SignTransactionFlow;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.utilities.ProgressTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.corda.core.contracts.ContractsDSL.requireThat;

@InitiatedBy(DataFlowInitiator.class)
public class DataFlowResponder extends DataFlow {
    private static final Logger logger = LoggerFactory.getLogger(DataFlowResponder.class);
    private FlowSession counterpartySession;

    public DataFlowResponder(FlowSession counterpartySession) {
        this.counterpartySession = counterpartySession;
    }

    @Override
    @Suspendable
    public SignedTransaction call() throws FlowException {
        class SignTxFlow extends SignTransactionFlow {
            private SignTxFlow(FlowSession otherPartyFlow, ProgressTracker progressTracker) {
                super(otherPartyFlow, progressTracker);
            }

            @Override
            protected void checkTransaction(SignedTransaction stx) {
                requireThat(require -> {

                    return null;
                });
            }
        }

        return subFlow(new SignTxFlow(counterpartySession, SignTransactionFlow.Companion.tracker()));
    }
}
