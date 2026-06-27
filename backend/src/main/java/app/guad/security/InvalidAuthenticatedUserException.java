package app.guad.security;

public class InvalidAuthenticatedUserException extends RuntimeException {
    public InvalidAuthenticatedUserException(String message) {
        super(message);
    }
}
