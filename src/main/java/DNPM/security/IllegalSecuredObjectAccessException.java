package DNPM.security;

public class IllegalSecuredObjectAccessException extends RuntimeException {

    public IllegalSecuredObjectAccessException() {
        super();
    }

    public IllegalSecuredObjectAccessException(String message) {
        super(message);
    }

}
