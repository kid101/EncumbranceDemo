package net.corda.demo.sc.state;

import net.corda.core.contracts.CommandAndState;
import net.corda.core.contracts.OwnableState;
import net.corda.core.identity.AbstractParty;
import net.corda.core.serialization.ConstructorForDeserialization;
import net.corda.demo.sc.contract.ExpiryContract;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

public class Expiry implements OwnableState {

    private Instant expiry;
    private String cakeId;
    private AbstractParty owner;

    @ConstructorForDeserialization
    public Expiry(Instant expiry, String cakeId, AbstractParty owner) {
        this.expiry = expiry;
        this.cakeId = cakeId;
        this.owner = owner;
    }


    public Expiry(Expiry other) {
        this.expiry = other.expiry;
        this.cakeId = other.cakeId;
        this.owner = other.owner;
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(owner);
    }

    public Instant getExpiry() {
        return expiry;
    }

    public String getCakeId() {
        return cakeId;
    }

    @NotNull
    @Override
    public AbstractParty getOwner() {
        return owner;
    }

    @NotNull
    @Override
    public CommandAndState withNewOwner(AbstractParty newOwner) {
        return new CommandAndState(new ExpiryContract.Commands.Pass(), new Expiry(this.expiry, cakeId, newOwner));
    }
}
