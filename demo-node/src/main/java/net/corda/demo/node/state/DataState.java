package net.corda.demo.node.state;

import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.serialization.ConstructorForDeserialization;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class DataState implements LinearState {
    private UniqueIdentifier dataId;
    private Party owner;
    private Party receiver;
    private byte[] data;

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return dataId;
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(receiver, owner);
    }
    @ConstructorForDeserialization
    public DataState(UniqueIdentifier dataId, Party owner, Party receiver, byte[] data) {
        this.dataId = dataId;
        this.owner = owner;
        this.receiver = receiver;
        this.data = data;
    }

    public DataState(Party owner, Party receiver, byte[] data) {
        this.dataId = new UniqueIdentifier();
        this.owner = owner;
        this.receiver = receiver;
        this.data = data;
    }

    public UniqueIdentifier getDataId() {
        return dataId;
    }

    public Party getOwner() {
        return owner;
    }

    public Party getReceiver() {
        return receiver;
    }

    public byte[] getData() {
        return data;
    }
}
