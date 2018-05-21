package net.corda.demo.server.helper;

import net.corda.core.node.services.Vault;
import net.corda.demo.node.state.HelloState;
import net.corda.demo.server.bo.HelloBO;

import java.util.ArrayList;
import java.util.List;

public class ControllerHelper {
    public static HelloBO convertStateToBO(HelloState helloState, Vault.StateStatus stateTypes) {
        HelloBO helloBO = new HelloBO();
        helloBO.setData(helloState.getData());
        helloBO.setHelloId(helloState.getHelloId().toString());
        List<String> receivers=new ArrayList<>();
        helloState.getReceivers().stream().forEach(e->receivers.add(e.toString()));
        helloBO.setReceivers(receivers);
        helloBO.setSender(helloState.getSender().toString());
        helloBO.setStatus(stateTypes.toString());
        return helloBO;
    }
}
