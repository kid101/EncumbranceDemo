package net.corda.demo.server.controller;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.node.NodeInfo;
import net.corda.demo.server.constant.ServerConstant;
import net.corda.demo.server.rpc.RPConnector;
import net.corda.demo.server.service.impl.DataFlowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/sc")
public class DemoController {
    private static final Logger logger = LoggerFactory.getLogger(DemoController.class);
    private final List<String> serviceNames = ImmutableList.of(ServerConstant.NOTARY, ServerConstant.ORACLE);
    @Autowired
    private RPConnector connector;
    @Autowired
    private DataFlowService dataFlowService;

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello!";
    }

    @GetMapping("/allNodes")
    public Map<String, List<CordaX500Name>> getAllNodes() {
        List<NodeInfo> nodeInfoSnapshot = connector.getRPCops().networkMapSnapshot();
        return ImmutableMap.of("allnodes",
                nodeInfoSnapshot.stream().map(node -> node.getLegalIdentities().get(0).getName()).filter(
                        name -> !name.equals(connector.getRPCops().nodeInfo().getLegalIdentities().get(0).getName())
                                && !serviceNames.contains(name.getOrganisation()))
                        .collect(toList()));
    }

    @GetMapping("me")
    public Map<String, CordaX500Name> getMyIdentity() {
        return ImmutableMap.of(ServerConstant.ME, connector.getRPCops().nodeInfo().getLegalIdentities().get(0).getName());
    }
}