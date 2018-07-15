package net.corda.demo.common.exception;

import net.corda.core.CordaRuntimeException;

public class NoCakeFoundException extends CordaRuntimeException {
    public NoCakeFoundException(String message) {
        super(message);
    }
}
