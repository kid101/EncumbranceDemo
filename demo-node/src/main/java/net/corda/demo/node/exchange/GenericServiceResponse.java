package net.corda.demo.node.exchange;

import net.corda.core.serialization.ConstructorForDeserialization;
import net.corda.core.serialization.CordaSerializable;

@CordaSerializable
public class GenericServiceResponse {
    private String status;
    private String data;
    private byte[] byteData;
    private String error;


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
    @ConstructorForDeserialization
    public GenericServiceResponse(String status, String data, byte[] byteData, String error) {
        this.status = status;
        this.data = data;
        this.byteData = byteData;
        this.error = error;
    }

    public GenericServiceResponse() {
    }

    @Override
    public String toString() {
        return "GenericServiceResponse [status=" + status + ", data=" + data + ", error=" + error + "]";
    }

    public byte[] getByteData() {
        return byteData;
    }

    public void setByteData(byte[] byteData) {
        this.byteData = byteData;
    }
}
