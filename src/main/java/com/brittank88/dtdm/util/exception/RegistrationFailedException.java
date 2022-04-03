package com.brittank88.dtdm.util.exception;

public class RegistrationFailedException extends Exception {

    public enum REGISTRY_TYPE { COMMAND, SUGGESTION_SUPPLIER }
    private final REGISTRY_TYPE registryType;

    public RegistrationFailedException(final String message, final REGISTRY_TYPE registryType) {
        super(message);
        this.registryType = registryType;
    }
    public RegistrationFailedException(final String message, final Throwable cause, final REGISTRY_TYPE registryType) {
        super(message, cause);
        this.registryType = registryType;
    }
    public RegistrationFailedException(final Throwable cause, final REGISTRY_TYPE registryType) {
        super(cause);
        this.registryType = registryType;
    }
    protected RegistrationFailedException(final String message, final Throwable cause, final REGISTRY_TYPE registryType, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace); this.registryType = registryType;
    }

    public final REGISTRY_TYPE getRegistryType() { return registryType; }
}
