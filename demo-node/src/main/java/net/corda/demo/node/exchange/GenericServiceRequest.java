package net.corda.demo.node.exchange;

import net.corda.core.serialization.ConstructorForDeserialization;
import net.corda.core.serialization.CordaSerializable;

@CordaSerializable
public class GenericServiceRequest {
    private String param;
    private String url;
    private String method;

    @ConstructorForDeserialization
    public GenericServiceRequest(String param, String url, String method) {
        this.param = param;
        this.url = url;
        this.method = method;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
