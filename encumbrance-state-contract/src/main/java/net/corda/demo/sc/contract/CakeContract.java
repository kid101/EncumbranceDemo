package net.corda.demo.sc.contract;

import net.corda.core.contracts.*;
import net.corda.core.transactions.LedgerTransaction;
import net.corda.demo.sc.state.Cake;
import net.corda.demo.sc.state.Expiry;

import java.security.PublicKey;
import java.util.List;

import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;
import static net.corda.core.contracts.ContractsDSL.requireThat;

public class CakeContract implements Contract {

    public static final String CAKE_CONTRACT_ID = "net.corda.demo.sc.contract.CakeContract";

    @Override
    public void verify(LedgerTransaction tx) {
        CommandWithParties<Commands> commandWithParties = requireSingleCommand(tx.getCommands(), Commands.class);
        Commands value = commandWithParties.getValue();
        List<PublicKey> signers = commandWithParties.getSigners();

        if (value instanceof Commands.Create) {
            requireThat(require -> {
                require.using("No inputs should be consumed.",
                        tx.getInputs().isEmpty());
                require.using("Two output states should be created.",
                        tx.getOutputStates().size() == 2);
                require.using("One expiry state should be created.",
                        tx.outputsOfType(Expiry.class).size() == 1);
                require.using("One cake state should be created.",
                        tx.outputsOfType(Cake.class).size() == 1);

                Cake outputCake = tx.outputsOfType(Cake.class).get(0);
                require.using("Owner must be required signer.",
                        signers.contains(outputCake.getOwner().getOwningKey()));

                return null;
            });

        } else if (value instanceof Commands.Sell) {
            requireThat(require -> {
                require.using("Two inputs should be consumed.",
                        tx.getInputs().size() == 2);
                require.using("One expiry state should be consumed.",
                        tx.inputsOfType(Expiry.class).size() == 1);
                require.using("One cake state should be consumed.",
                        tx.inputsOfType(Cake.class).size() == 1);

                require.using("Two outputs should be created.",
                        tx.getInputs().size() == 2);
                require.using("One expiry state should be created.",
                        tx.outputsOfType(Expiry.class).size() == 1);
                require.using("One cake state should be created.",
                        tx.outputsOfType(Cake.class).size() == 1);

                Cake outputCake = tx.outputsOfType(Cake.class).stream().findFirst().orElseThrow(() -> new IllegalArgumentException("no Cake Output found at the time of Selling cake."));
                Cake inputCake = tx.inputsOfType(Cake.class).stream().findFirst().orElseThrow(() -> new IllegalArgumentException("no Cake Input found at the time of Selling cake."));
                require.using("Output cake must be input cake.",
                        outputCake.equals(inputCake));
                require.using("Output cake owner must be required signer.",
                        signers.contains(outputCake.getOwner().getOwningKey()));
                require.using("Input cake owner must be required signer.",
                        signers.contains(inputCake.getOwner().getOwningKey()));

                return null;
            });

        } else if (value instanceof Commands.Consume) {
            requireThat(require -> {
                require.using("No outputs should be created.",
                        tx.getOutputs().isEmpty());
                require.using("Two input states should be consumed.",
                        tx.getInputStates().size() == 2);
                require.using("One expiry state should be consumed.",
                        tx.inputsOfType(Expiry.class).size() == 1);
                require.using("One cake state should be consumed.",
                        tx.inputsOfType(Cake.class).size() == 1);

                Cake inputCake = tx.inputsOfType(Cake.class).stream().findFirst().orElseThrow(() -> new IllegalArgumentException("no Cake Input found at the time of Selling cake."));
                require.using("Owner must be required signer.",
                        signers.contains(inputCake.getOwner().getOwningKey()));

                return null;
            });

        } else {
            throw new IllegalArgumentException("Unrecognised command.");
        }
    }

    public interface Commands extends CommandData {
        class Create extends TypeOnlyCommandData implements Commands { }

        class Sell extends TypeOnlyCommandData implements Commands { }

        class Consume extends TypeOnlyCommandData implements Commands { }
    }
}
