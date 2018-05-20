package net.corda.demo.node.cordaservice;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.StartableByRPC;
import net.corda.demo.node.exception.DemoFlowException;
import net.corda.demo.node.exchange.GenericServiceRequest;
import net.corda.demo.node.exchange.GenericServiceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@StartableByRPC
public class EtherealServiceExecutor extends FlowLogic<Void> {
    private static final Logger logger = LoggerFactory.getLogger(EtherealServiceExecutor.class);
    private GenericServiceRequest request;

    public EtherealServiceExecutor(GenericServiceRequest request) {
        this.request = request;
    }

    @Suspendable
    @Override
    public Void call() throws FlowException {
        GenericServiceResponse genericServiceResponse = getServiceHub().cordaService(EtherealService.class).executeRequest(request);
        if (genericServiceResponse.getByteData() != null) {
            try {
                Files.write(new File("./response/readme.txt").toPath(), genericServiceResponse.getByteData());
            } catch (IOException e) {
                logger.error(e.getMessage());
                throw new DemoFlowException(e.getMessage(), e.getCause());
            }
        }
        return null;
    }
}

