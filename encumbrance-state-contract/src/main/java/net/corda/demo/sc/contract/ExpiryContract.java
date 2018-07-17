package net.corda.demo.sc.contract;

import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.TimeWindow;
import net.corda.core.contracts.TypeOnlyCommandData;
import net.corda.core.transactions.LedgerTransaction;
import net.corda.demo.sc.state.Expiry;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class ExpiryContract implements Contract {
    public static final String EXPIRY_CONTRACT_ID = "net.corda.demo.sc.contract.ExpiryContract";

    @Override
    public void verify(LedgerTransaction tx) {
        Expiry expiry;
        if (tx.getCommands().stream().anyMatch(e -> e.getValue() instanceof CakeContract.Commands.Consume))
            expiry = tx.inputsOfType(Expiry.class).get(0);
        else
            expiry = tx.outputsOfType(Expiry.class).get(0);

        TimeWindow timeWindow = tx.getTimeWindow();
        if (timeWindow == null || timeWindow.getUntilTime() == null) {
            throw new IllegalArgumentException("Cake transaction must have a timestamp with an until-time.");
        }
        if (timeWindow.getUntilTime().isAfter(expiry.getExpiry())) {
            throw new IllegalArgumentException("Expiry has passed! Expiry date & time was: " + LocalDateTime.ofInstant(expiry.getExpiry(), ZoneId.systemDefault()));
        }
    }

    public interface Commands extends CommandData {
        class Create extends TypeOnlyCommandData implements Commands {
        }

        class Pass extends TypeOnlyCommandData implements Commands {
        }
    }
}
