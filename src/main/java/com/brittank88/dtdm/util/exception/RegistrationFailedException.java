package com.brittank88.dtdm.util.exception;

public class RegistrationFailedException extends Exception {

    public enum REGISTRY_TYPE { COMMAND, SUGGESTION_SUPPLIER }
    private final REGISTRY_TYPE registryType;

    public RegistrationFailedException(String message, REGISTRY_TYPE registryType) {
        super(message);
        this.registryType = registryType;
    }
    public RegistrationFailedException(String message, Throwable cause, REGISTRY_TYPE registryType) {
        super(message, cause);
        this.registryType = registryType;
    }
    public RegistrationFailedException(Throwable cause, REGISTRY_TYPE registryType) {
        super(cause);
        this.registryType = registryType;
    }
    protected RegistrationFailedException(String message, Throwable cause, REGISTRY_TYPE registryType, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace); this.registryType = registryType;
    }

    public REGISTRY_TYPE getRegistryType() { return registryType; }
}
