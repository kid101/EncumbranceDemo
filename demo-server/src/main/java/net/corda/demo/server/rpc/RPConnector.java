package net.corda.demo.server.rpc;

import net.corda.client.rpc.CordaRPCClient;
import net.corda.client.rpc.CordaRPCConnection;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.utilities.NetworkHostAndPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class RPConnector {
    @Value("${config.rpc.address}")
    private String rpcAddress;
    @Value("${config.rpc.username}")
    private String username;
    @Value("${config.rpc.password}")
    private String password;

    private CordaRPCOps rpcOps;

    @PostConstruct
    private void init() {
        System.out.println(
                String.format("RPConnector [host=%s, username=%s, password=%s]", rpcAddress, username, password)
        );

        NetworkHostAndPort hostAndPort = NetworkHostAndPort.parse(rpcAddress);
        CordaRPCClient rpcClient = new CordaRPCClient(hostAndPort);
        CordaRPCConnection rpcConnection = rpcClient.start(username, password);
        rpcOps = rpcConnection.getProxy();

        System.out.println("connected to via RPC");
    }

    public CordaRPCOps getRPCops() {
        return rpcOps;
    }
}
