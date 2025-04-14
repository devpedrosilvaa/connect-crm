package tech.silva.connectcrm.exceptions;

public class EntityNotAvailableForViewException extends RuntimeException {
    public EntityNotAvailableForViewException(String message) {
        super(message);
    }
}
