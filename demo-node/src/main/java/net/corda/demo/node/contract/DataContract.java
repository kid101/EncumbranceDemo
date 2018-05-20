package net.corda.demo.node.contract;

import net.corda.demo.node.state.DataState;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.TypeOnlyCommandData;
import net.corda.core.transactions.LedgerTransaction;

import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;
import static net.corda.core.contracts.ContractsDSL.requireThat;

public class DataContract implements Contract {
    // This is used to identify our contract when building a transaction.
    public static final String ACCOUNT_CONTRACT_ID = "net.corda.demo.node.contract.DataContract";

    /*
     A transaction is considered valid if the verify() function of the contract of each of the transaction's input
     and output states does not throw an exception.
     */

    @Override
    public void verify(LedgerTransaction tx) {
        //Verification to be added
        CommandWithParties<Commands> commandWithParties = requireSingleCommand(tx.getCommands(), Commands.class);
        Commands value = commandWithParties.getValue();
        if (value instanceof Commands.Create) {
            requireThat(require -> {
                require.using("No inputs should be consumed.",
                        tx.getInputs().isEmpty());
                require.using("At least one output state should be created.",
                        tx.getOutputs().size() >= 1);
                return null;
            });
        }

        if (value instanceof Commands.Update) {
            requireThat(require -> {
                require.using("Previous state should be consumed.",
                        tx.getInputs().size() == 1);
                require.using("New output state should be created.",
                        tx.getOutputs().size()>=1);
                return null;
            });
        }

    }

    public interface Commands extends CommandData {
        class Create extends TypeOnlyCommandData implements Commands {
        }

        class Update extends TypeOnlyCommandData implements Commands {
        }

    }
}
