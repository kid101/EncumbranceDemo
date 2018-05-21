package net.corda.demo.node.cordaservice;

import net.corda.core.flows.FlowException;
import net.corda.core.node.AppServiceHub;
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

import static net.corda.demo.node.constant.ServiceConstant.CACHE_FOLDER_PATH;
import static net.corda.demo.node.constant.ServiceConstant.RESPONSE_FOLDER_PATH;

// Can be Later Deployed as an Independent Oracle Service as well If required.
@CordaService
public class EtherealService extends SingletonSerializeAsToken {
    private static final Logger logger = LoggerFactory.getLogger(EtherealService.class);

    private final ServiceHub services;
    private File cacheDir;

    public EtherealService(AppServiceHub services) {
        this.services = services;
        File cacheDir = new File(CACHE_FOLDER_PATH);
        File responseDir = new File(RESPONSE_FOLDER_PATH);
        boolean rDir = responseDir.mkdir();
        boolean cDir = cacheDir.mkdir();
        if (cacheDir.exists()) {
            this.cacheDir = cacheDir;
        }
    }

    public ServiceHub getServices() {
        return services;
    }

    public GenericServiceResponse executeRequest(GenericServiceRequest request) throws FlowException {
        HttpInvoker factory = HttpInvokerFactory.getHttpInvoker(request.getMethod(), cacheDir);
        return factory.executeRequest(request.getUrl());
    }
}
