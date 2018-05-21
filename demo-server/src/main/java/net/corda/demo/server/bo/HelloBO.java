package net.corda.demo.server.bo;

import java.util.List;

public class HelloBO {
    private String sender;
    private List<String> receivers;
    private String data;
    private String helloId;
    private String status;

    public HelloBO(String sender, List<String> receivers, String data, String helloId, String status) {
        this.sender = sender;
        this.receivers = receivers;
        this.data = data;
        this.helloId = helloId;
        this.status = status;
    }

    public HelloBO() {
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public List<String> getReceivers() {
        return receivers;
    }

    public void setReceivers(List<String> receivers) {
        this.receivers = receivers;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHelloId() {
        return helloId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setHelloId(String helloId) {
        this.helloId = helloId;
    }
}
