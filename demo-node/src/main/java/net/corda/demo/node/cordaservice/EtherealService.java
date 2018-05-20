package net.corda.demo.node.cordaservice;

import net.corda.core.flows.FlowException;
import net.corda.core.node.ServiceHub;
import net.corda.core.node.services.CordaService;
import net.corda.core.serialization.SingletonSerializeAsToken;
import net.corda.demo.node.exchange.GenericServiceRequest;
import net.corda.demo.node.exchange.GenericServiceResponse;
import net.corda.demo.node.invokers.HttpInvoker;
import net.corda.demo.node.invokers.HttpInvokerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.security.PublicKey;

// Can be Later Deployed as an Independent Oracle Service as well If required.
@CordaService
public class EtherealService extends SingletonSerializeAsToken {
    private static final Logger logger = LoggerFactory.getLogger(EtherealService.class);
    private final ServiceHub services;
    private PublicKey myKey;
    private File cacheDir;

    public EtherealService(ServiceHub services) {
        this.services = services;
        myKey = services.getMyInfo().getLegalIdentities().get(0).getOwningKey();
        File cacheDir = new File("./cache");
        File responseDir = new File("./response");
        boolean rDir = responseDir.mkdir();
        boolean cDir = cacheDir.mkdir();
        if (cacheDir.exists()) {
            this.cacheDir = cacheDir;
        }

    }

    public ServiceHub getServices() {
        return services;
    }

    public PublicKey getMyKey() {
        return myKey;
    }

    public GenericServiceResponse executeRequest(GenericServiceRequest request) throws FlowException {
        HttpInvoker factory = HttpInvokerFactory.getHttpInvoker(request.getMethod(), cacheDir);
        return factory.executeRequest(request.getUrl());
    }
}
