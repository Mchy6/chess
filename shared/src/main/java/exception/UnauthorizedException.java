package exception;

public class UnauthorizedException extends Exception {
    final private String message;
    public UnauthorizedException(String message) {
        super(message);
        this.message = message;
    }
    public String getMessage() {
        return message;
    }

}
