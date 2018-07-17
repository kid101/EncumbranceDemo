package net.corda.demo.sc.state;

import net.corda.core.contracts.CommandAndState;
import net.corda.core.contracts.OwnableState;
import net.corda.core.identity.AbstractParty;
import net.corda.core.serialization.ConstructorForDeserialization;
import net.corda.demo.sc.contract.ExpiryContract;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

public class Expiry implements OwnableState {
    private final Instant expiry;
    private final String cakeId;
    private final AbstractParty owner;

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
        return Collections.singletonList(owner);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Expiry expiry1 = (Expiry) o;

        if (expiry != null ? !expiry.equals(expiry1.expiry) : expiry1.expiry != null) return false;
        return cakeId != null ? cakeId.equals(expiry1.cakeId) : expiry1.cakeId == null;
    }

    @Override
    public int hashCode() {
        int result = expiry != null ? expiry.hashCode() : 0;
        result = 31 * result + (cakeId != null ? cakeId.hashCode() : 0);
        return result;
    }
}
