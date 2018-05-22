package net.corda.demo.node.flow;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.StateAndContract;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.demo.node.constant.ServiceConstant;
import net.corda.demo.node.contract.HelloContract;
import net.corda.demo.node.exception.DemoFlowException;
import net.corda.demo.node.state.HelloState;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@StartableByRPC
@InitiatingFlow
public class SayHelloFlow extends FlowLogic<SignedTransaction> {
    private HelloContract.Commands command;

    public SayHelloFlow(HelloContract.Commands command) {
        this.command = command;
    }

    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {
        final Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

        // Load the file form the response Folder
        List<Party> counterPartyList;
        try (BufferedReader br = Files.newBufferedReader(Paths.get(ServiceConstant.PARTY_LIST_PATH))) {
            counterPartyList = br.lines().map(e -> getServiceHub().getIdentityService().partiesFromName(e, true).iterator().next()).collect(Collectors.toList());
        } catch (IOException e) {
            getLogger().error("Unable to find specified Party! " + e.getMessage());
            e.printStackTrace();
            throw new DemoFlowException("Invalid Party in the partyFileList" + e.getMessage());
        }
        HelloState helloState = new HelloState(getOurIdentity(), counterPartyList, "hello!");

        // Create appropriate Command.
        Command<HelloContract.Commands> txCommand = getTxCommand(command, helloState);

        TransactionBuilder txBuilder = new TransactionBuilder(notary)
                .withItems(new StateAndContract(helloState, HelloContract.HELLO_CONTRACT_ID), txCommand);

        // Stage 2.
        // Verify the transaction.
        txBuilder.verify(getServiceHub());

        // Stage 3.
        // Sign the transaction.
        final SignedTransaction partSignedTx = getServiceHub().signInitialTransaction(txBuilder);
        // Initiate Counter Party Sessions
        Set<FlowSession> counterPartySessions = new HashSet<>();
        counterPartyList.forEach(e -> {
            if (!e.equals(getOurIdentity())) counterPartySessions.add(initiateFlow(e));
        });

        // Stage 4.
        // Send the state to the counterparty, and receive it back with their signature.
        final SignedTransaction fullySignedTx = subFlow(
                new CollectSignaturesFlow(partSignedTx, counterPartySessions));

        // Stage 5.
        // Notarise and record the transaction in both parties' vaults.
        return subFlow(new FinalityFlow(fullySignedTx));
    }

    @NotNull
    private Command<HelloContract.Commands> getTxCommand(HelloContract.Commands command, HelloState newHelloState) {
        if (command instanceof HelloContract.Commands.Create)
            return new Command<>(new HelloContract.Commands.Create(), getOurIdentity().getOwningKey());
        throw new DemoFlowException("Unidentifiable or unimplemented command! : " + command.toString());
    }
}

