package net.corda.demo.server.exception;

public class CordaFlowException extends RuntimeException {
    public CordaFlowException() {
    }

    public CordaFlowException(String message) {
        super(message);
    }

    public CordaFlowException(String message, Throwable cause) {
        super(message, cause);
    }

    public CordaFlowException(Throwable cause) {
        super(cause);
    }

    public CordaFlowException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
