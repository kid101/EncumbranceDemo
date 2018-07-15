package net.corda.demo.common.exception;

import net.corda.core.CordaRuntimeException;

public class AlreadyIssuedCakeIdException extends CordaRuntimeException {
    public AlreadyIssuedCakeIdException(String message) {
        super(message);
    }
}
