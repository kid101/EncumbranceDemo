package net.corda.demo.sc.contract;

import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.TypeOnlyCommandData;
import net.corda.core.transactions.LedgerTransaction;
import net.corda.demo.sc.state.Cake;
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
                        tx.getOutputStates().size() > 1);
                require.using("Tx should've Expiry at index 1 in output",
                        tx.getOutputs().get(1).getData() instanceof Expiry);
                require.using("no Expiry found at the time of creating cake.",
                        tx.outputsOfType(Expiry.class).size() != 0);
                return null;
            });
        }

        if (value instanceof Commands.Sell) {
            Expiry outputExpiry = tx.outputsOfType(Expiry.class).stream().findFirst().orElseThrow(() -> new IllegalArgumentException("no Cake Output found at the time of Selling cake."));
            Expiry inputExpiry = tx.inputsOfType(Expiry.class).stream().findFirst().orElseThrow(() -> new IllegalArgumentException("no Expiry Input found at the time of Selling cake."));
            Cake outputCake= tx.outputsOfType(Cake.class).stream().findFirst().orElseThrow(() -> new IllegalArgumentException("no Cake Output found at the time of Selling cake."));
            Cake inputCake = tx.inputsOfType(Cake.class).stream().findFirst().orElseThrow(() -> new IllegalArgumentException("no Cake Input found at the time of Selling cake."));
            requireThat(require -> {
                require.using("Previous state should be consumed.",
                        tx.getInputs().size() >= 1);
                require.using("New output state should be created.",
                        tx.getOutputs().size() >= 1);
                require.using("Tx should've Expiry at index 1 in output",
                        tx.getOutputs().get(1).getData() instanceof Expiry);
                require.using("input an output Expiry in the Tx should be same.",
                        inputExpiry.equals(outputExpiry));
                require.using("input an output Cake in the Tx should be same.",
                        inputCake.equals(outputCake));
                return null;
            });
        }

        if (value instanceof Commands.Consume) {

            requireThat(require -> {
                require.using("Cake should be consumed.",
                        tx.getInputs().size() >= 1);
                require.using("New output state should not be created.",
                        tx.getOutputs().size() <= 0);
                require.using("Tx should've Expiry at index 1 in Input",
                        tx.getInputs().get(1).getState().getData() instanceof Expiry);
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
