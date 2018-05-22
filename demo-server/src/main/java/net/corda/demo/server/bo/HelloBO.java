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

    public String getSender() {
        return sender;
    }

    public List<String> getReceivers() {
        return receivers;
    }

    public String getData() {
        return data;
    }

    public String getHelloId() {
        return helloId;
    }

    public String getStatus() {
        return status;
    }
}
