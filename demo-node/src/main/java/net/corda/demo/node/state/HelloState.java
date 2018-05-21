package net.corda.demo.node.state;

import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.serialization.ConstructorForDeserialization;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HelloState implements LinearState {
    private UniqueIdentifier helloId;
    private Party sender;
    private List<Party> receivers;
    private String data;

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return helloId;
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        List<AbstractParty> participantList = new ArrayList<>();
        participantList.add(sender);
        participantList.addAll(receivers);
        return participantList;
    }

    @ConstructorForDeserialization
    public HelloState(UniqueIdentifier helloId, Party sender, List<Party> receivers, String data) {
        this.helloId = helloId;
        this.sender = sender;
        this.receivers = receivers;
        this.data = data;
    }

    public HelloState(Party sender, List<Party> receivers, String data) {
        this.helloId = new UniqueIdentifier();
        this.sender = sender;
        this.receivers = receivers;
        this.data = data;
    }

    public UniqueIdentifier getHelloId() {
        return helloId;
    }

    public Party getSender() {
        return sender;
    }

    public List<Party> getReceivers() {
        return receivers;
    }

    public String getData() {
        return data;
    }
}
