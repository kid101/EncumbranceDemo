package net.corda.demo.node.exception;

public class DemoFlowException extends RuntimeException {
    public DemoFlowException(String message) {
        super(message);
    }

    public DemoFlowException(String message, Throwable cause) {
        super(message, cause);
    }
}
