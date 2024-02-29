package exception;

public class BadRequestException extends Exception {
    final private String message;
    public BadRequestException(String message) {
        super(message);
        this.message = message;
    }
    public String getMessage() {
        return message;
    }

}
