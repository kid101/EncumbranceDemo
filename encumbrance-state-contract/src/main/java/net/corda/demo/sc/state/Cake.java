package net.corda.demo.sc.state;

import net.corda.core.contracts.CommandAndState;
import net.corda.core.contracts.OwnableState;
import net.corda.core.identity.AbstractParty;
import net.corda.core.serialization.ConstructorForDeserialization;
import net.corda.demo.sc.contract.CakeContract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class Cake implements OwnableState {

    private Flavour flavour;
    private AbstractParty owner;
    private String cakeId;

    @ConstructorForDeserialization
    public Cake(Flavour flavour, AbstractParty owner, String cakeId) {
        this.flavour = flavour;
        this.owner = owner;
        this.cakeId = cakeId;
    }

    public Cake(Cake other) {
        this.flavour = other.flavour;
        this.owner = other.owner;
        this.cakeId = other.cakeId;
    }

    @NotNull
    @Override
    public AbstractParty getOwner() {
        return owner;
    }

    @NotNull
    @Override
    public CommandAndState withNewOwner(AbstractParty newOwner) {
        return new CommandAndState(new CakeContract.Commands.Sell(), new Cake(this.flavour, newOwner, cakeId));
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(owner);
    }

    public Flavour getFlavour() {
        return flavour;
    }

    public String getCakeId() {
        return cakeId;
    }
}
