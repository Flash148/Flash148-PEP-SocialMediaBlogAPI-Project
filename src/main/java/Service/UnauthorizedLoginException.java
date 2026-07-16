package Service;

public class UnauthorizedLoginException extends RuntimeException {
    public UnauthorizedLoginException(String message) {
        super(message);
    }
}
