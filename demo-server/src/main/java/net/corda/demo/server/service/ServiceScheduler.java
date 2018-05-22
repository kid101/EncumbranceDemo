package net.corda.demo.server.service;

import net.corda.core.messaging.FlowHandle;
import net.corda.core.transactions.SignedTransaction;
import net.corda.demo.node.contract.HelloContract;
import net.corda.demo.node.cordaservice.EtherealServiceExecutor;
import net.corda.demo.node.exchange.GenericServiceRequest;
import net.corda.demo.node.flow.SayHelloFlow;
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
        GenericServiceRequest serviceRequest = new GenericServiceRequest("", ServerConstant.PARTY_LIST, HttpMethod.GET);
        FlowHandle<Void> flowHandle = connector.getRPCops().startFlowDynamic(EtherealServiceExecutor.class, serviceRequest);
        // Now look at the downloaded file and send hello to the respective counterparties.
        FlowHandle<SignedTransaction> sayHelloFlowHandle = connector.getRPCops().startFlowDynamic(SayHelloFlow.class, new HelloContract.Commands.Create());
        try {
            flowHandle.getReturnValue().get();
            SignedTransaction signedTransaction = sayHelloFlowHandle.getReturnValue().get();
            logger.info("Hello Sent with secureHash: " + signedTransaction.getId());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        logger.info("ServiceScheduler: End");
    }
}