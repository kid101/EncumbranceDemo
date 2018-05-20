package net.corda.demo.server.service.impl;

import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.identity.Party;
import net.corda.core.messaging.FlowHandle;
import net.corda.core.transactions.SignedTransaction;
import net.corda.demo.node.flow.initiator.DataFlowInitiator;
import net.corda.demo.node.state.DataState;
import net.corda.demo.server.exception.CordaFlowException;
import net.corda.demo.server.rpc.RPConnector;
import net.corda.demo.server.service.ICordaFlowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ExecutionException;

public class DataFlowService implements ICordaFlowService<DataState, SignedTransaction> {
    private static final Logger logger = LoggerFactory.getLogger(DataFlowService.class);
    @Autowired
    private RPConnector rpcOps;

    public SignedTransaction transformAndInitiateFlow(Party owner, Party receiver, byte[] data, CommandData command) {
        DataState dataState = new DataState(owner,receiver,data);
        return this.initiateFlow(dataState, command);
    }

    @Override
    public SignedTransaction initiateFlow(DataState dataState, CommandData command) {
        FlowHandle<SignedTransaction> signedTransactionFlowHandle = rpcOps.getRPCops().startFlowDynamic(DataFlowInitiator.class, dataState);
        try {
            return signedTransactionFlowHandle.getReturnValue().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw new CordaFlowException(e.getMessage(), e.getCause());
        } catch (ExecutionException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw new CordaFlowException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public SignedTransaction updateExistingStateFlow(StateAndRef<? extends ContractState> oldState, DataState newState, CommandData command) {
        return null;
    }

}

