package net.corda.demo.server.service;


import net.corda.core.messaging.FlowHandle;
import net.corda.demo.node.exchange.GenericServiceRequest;
import net.corda.demo.node.cordaservice.EtherealServiceExecutor;
import net.corda.demo.server.constant.ServerConstant;
import net.corda.demo.server.rpc.RPConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import javax.ws.rs.HttpMethod;

public class ServiceScheduler {
    @Autowired
    RPConnector connector;
    private static final Logger logger = LoggerFactory.getLogger(ServiceScheduler.class);

    @Scheduled(fixedRate = 600_000) //Approx 10 minutes
    public void startService() {
        logger.info("ServiceScheduler: Start");
        GenericServiceRequest serviceRequest = new GenericServiceRequest("", ServerConstant.BITCOIN_READ_ME, HttpMethod.GET);
        FlowHandle<Void> flowHandle = connector.getRPCops().startFlowDynamic(EtherealServiceExecutor.class, serviceRequest);
        try {
            flowHandle.getReturnValue().get();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        logger.info("ServiceScheduler: End");
    }
}