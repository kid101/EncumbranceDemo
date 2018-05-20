package net.corda.demo.node.flow.initiator;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.StateAndContract;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.*;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.demo.node.contract.DataContract;
import net.corda.demo.node.exception.DemoFlowException;
import net.corda.demo.node.flow.DataFlow;
import net.corda.demo.node.state.DataState;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@StartableByRPC
@InitiatingFlow
public class DataFlowInitiator extends DataFlow {
    private static final Logger logger = LoggerFactory.getLogger(DataFlowInitiator.class);
    private DataState newDataState;
    private StateAndRef<DataState> previousDataState;
    private DataContract.Commands command;


    public DataFlowInitiator(DataState newDataState, DataContract.Commands command, StateAndRef<DataState> previousDataState) {
        this.newDataState = newDataState;
        this.command = command;
        this.previousDataState = previousDataState;
    }

    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {
        final Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

        // Generate an unsigned transaction.
        TransactionBuilder txBuilder = null;
        // Create appropriate Command.
        Command<DataContract.Commands> txCommand = getTxCommand(command);
        if (command instanceof DataContract.Commands.Create) {
            txBuilder = new TransactionBuilder(notary)
                    .withItems(new StateAndContract(newDataState, DataContract.ACCOUNT_CONTRACT_ID), txCommand);
        } else if (command instanceof DataContract.Commands.Update) {
            txBuilder = new TransactionBuilder(notary)
                    .withItems(new StateAndContract(newDataState, DataContract.ACCOUNT_CONTRACT_ID), txCommand, previousDataState);
        } else throw new DemoFlowException("Unidentifiable command! : " + command.toString());

        try {
            // Stage 2.
            // Verify the transaction.
            txBuilder.verify(getServiceHub());
            // Stage 3.
            // Sign the transaction.
            final SignedTransaction partSignedTx = getServiceHub().signInitialTransaction(txBuilder);
            Set<FlowSession> allCounterPartySessions = new HashSet<>();
            Party currentParty = getOurIdentity();
            List<Party> allCounterParties;
            allCounterParties = getAllCounterParties(newDataState.getParticipants(), currentParty);
            for (Party counterParty : allCounterParties) {
                allCounterPartySessions.add(initiateFlow(counterParty));
            }

            // Stage 4.
            // Send the state to the counterparty, and receive it back with their signature.
            final SignedTransaction fullySignedTx = subFlow(
                    new CollectSignaturesFlow(partSignedTx, allCounterPartySessions));

            // Stage 5.
            // Notarise and record the transaction in both parties' vaults.
            return subFlow(new FinalityFlow(fullySignedTx));
        } catch (FlowException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            throw new FlowException(e.getMessage());
        }
    }

    @NotNull
    private Command<DataContract.Commands> getTxCommand(DataContract.Commands command) {
        if (command instanceof DataContract.Commands.Create)
            return new Command<>(new DataContract.Commands.Create(),
                    newDataState.getParticipants().stream().map(AbstractParty::getOwningKey).collect(Collectors.toList()));
        else if (command instanceof DataContract.Commands.Update)
            return new Command<>(new DataContract.Commands.Update(),
                    newDataState.getParticipants().stream().map(AbstractParty::getOwningKey).collect(Collectors.toList()));
        throw new DemoFlowException("Unidentifiable command! : " + command.toString());
    }
}

