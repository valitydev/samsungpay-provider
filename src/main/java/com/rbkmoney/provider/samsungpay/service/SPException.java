package com.rbkmoney.provider.samsungpay.service;

/**
 * Created by vpankrashkin on 12.04.18.
 */
public class SPException extends Exception {
    private String payload;

    public SPException() {
    }

    public SPException(String message, String payload) {
        super(message);
        this.payload = payload;
    }

    public SPException(String message) {
        super(message);
    }

    public SPException(String message, Throwable cause) {
        super(message, cause);
    }

    public SPException(Throwable cause) {
        super(cause);
    }

    public SPException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public String getPayload() {
        return payload;
    }
}
