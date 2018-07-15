package net.corda.demo.common.exception;

import net.corda.core.CordaRuntimeException;

public class NoExpiryFoundException extends CordaRuntimeException {
    public NoExpiryFoundException(String message) {
        super(message);
    }
}
