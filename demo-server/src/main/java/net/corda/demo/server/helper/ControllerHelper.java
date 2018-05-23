package net.corda.demo.server.helper;

import net.corda.core.node.services.Vault;
import net.corda.demo.node.state.HelloState;
import net.corda.demo.server.bo.HelloBO;

import java.util.ArrayList;
import java.util.List;

public class ControllerHelper {
    public static HelloBO convertStateToBO(HelloState helloState, Vault.StateStatus stateTypes) {
        List<String> receivers=new ArrayList<>();
        helloState.getReceivers().forEach(e -> receivers.add(e.toString()));

        return new HelloBO(
                helloState.getSender().toString(),
                receivers,
                helloState.getData(),
                helloState.getHelloId().toString(),
                stateTypes.toString()
        );
    }
}
