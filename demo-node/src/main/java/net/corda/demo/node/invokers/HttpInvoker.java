package net.corda.demo.node.invokers;

import net.corda.demo.node.exchange.GenericServiceResponse;

public interface HttpInvoker {

public GenericServiceResponse executeRequest(String URL);
}
