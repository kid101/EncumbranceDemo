package net.corda.demo.server.service;

import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.StateAndRef;

public interface ICordaFlowService<I,O> {

    public O initiateFlow(I i, CommandData command);
    public O updateExistingStateFlow(StateAndRef<? extends ContractState> oldState, I newState,CommandData command);
}
