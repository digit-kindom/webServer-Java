package com.async.digitkingdom.common.exception;

public class PasswordAuthException extends CommonException {
    public PasswordAuthException(String message, int code) {
        super(message, 401);
    }

    public PasswordAuthException(String message, Throwable cause, int code) {
        super(message, cause, 401);
    }

    public PasswordAuthException(Throwable cause, int code) {
        super(cause, 401);
    }
}
