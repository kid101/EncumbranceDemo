package net.corda.demo.node.exception;

public class DemoFlowException extends RuntimeException {
    public DemoFlowException() {
    }

    public DemoFlowException(String message) {
        super(message);
    }

    public DemoFlowException(String message, Throwable cause) {
        super(message, cause);
    }

    public DemoFlowException(Throwable cause) {
        super(cause);
    }

    public DemoFlowException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
