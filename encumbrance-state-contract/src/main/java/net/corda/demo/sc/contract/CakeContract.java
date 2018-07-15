package net.corda.demo.sc.contract;

import net.corda.core.CordaRuntimeException;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.TypeOnlyCommandData;
import net.corda.core.transactions.LedgerTransaction;
import net.corda.demo.sc.state.Expiry;

import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;
import static net.corda.core.contracts.ContractsDSL.requireThat;

public class CakeContract implements Contract {

    public static final String CAKE_CONTRACT_ID = "net.corda.demo.sc.contract.CakeContract";

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
                        tx.getOutputStates().size() > 1);// to verify that expiry of a cake is later than now.
                tx.outputsOfType(Expiry.class).stream().findAny().orElseThrow(() -> new CordaRuntimeException("no Expiry found at the time of creating cake."));
                return null;
            });
        }

        if (value instanceof Commands.Sell) {
            tx.outputsOfType(Expiry.class).stream().findAny().orElseThrow(() -> new CordaRuntimeException("no Expiry found at the time of Selling cake."));
            requireThat(require -> {
                require.using("Previous state should be consumed.",
                        tx.getInputs().size() >= 1);
                require.using("New output state should be created.",
                        tx.getOutputs().size() >= 1);
                return null;
            });
        }

        if (value instanceof Commands.Consume) {
            tx.inputsOfType(Expiry.class).stream().findAny().orElseThrow(() -> new CordaRuntimeException("no Expiry found at the time of Consuming cake."));
            requireThat(require -> {
                require.using("Cake should be consumed.",
                        tx.getInputs().size() >= 1);
                require.using("New output state should not be created.",
                        tx.getOutputs().size() <= 0);
                return null;
            });
        }
    }

    public interface Commands extends CommandData {
        class Create extends TypeOnlyCommandData implements Commands {
        }

        class Sell extends TypeOnlyCommandData implements Commands {
        }

        class Consume extends TypeOnlyCommandData implements Commands {
        }
    }
}
