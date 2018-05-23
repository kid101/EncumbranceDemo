package net.corda.demo.node.invokers;

import net.corda.demo.node.exception.DemoFlowException;
import net.corda.demo.node.invokerimpls.HttpGetInvoker;

import javax.ws.rs.HttpMethod;
import java.io.File;

public class HttpInvokerFactory {
    public static HttpInvoker getHttpInvoker(String  methodType, File cacheDir) {
        if (methodType.equals(HttpMethod.GET)) {
            return new HttpGetInvoker(cacheDir);
        } else
            throw new DemoFlowException("UnImplemented HttpMethod!: " + methodType);
    }
}
