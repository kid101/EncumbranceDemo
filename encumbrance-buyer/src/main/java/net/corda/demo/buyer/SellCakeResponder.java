package net.corda.demo.buyer;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowSession;
import net.corda.core.flows.InitiatedBy;
import net.corda.core.flows.SignTransactionFlow;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.utilities.ProgressTracker;
import net.corda.demo.common.exception.NoCakeFoundException;
import net.corda.demo.common.flow.SellCake;
import net.corda.demo.sc.state.Cake;

@InitiatedBy(SellCake.class)
public class SellCakeResponder extends SellCake{
        private final FlowSession counterpartySession;

        public SellCakeResponder(FlowSession counterpartySession) {
            this.counterpartySession = counterpartySession;
        }

        @Suspendable
        @Override
        public SignedTransaction call() throws FlowException {
            // This only gets called when we send or receive send and receive is called
            class SignTxFlow extends SignTransactionFlow {
                private SignTxFlow(FlowSession otherPartyFlow, ProgressTracker progressTracker) {
                    super(otherPartyFlow, progressTracker);
                }
                @Override
                protected void checkTransaction(SignedTransaction stx) {
                    stx.getTx().outputsOfType(Cake.class).stream().findAny().orElseThrow(() -> new NoCakeFoundException("No cake found in Tx"));
                }
            }
            try {
                return subFlow(new SignTxFlow(counterpartySession, SignTransactionFlow.Companion.tracker()));
            } catch (FlowException e) {
                logger.error(e.getMessage());
                throw new FlowException(e.getMessage());
            }
        }
}
