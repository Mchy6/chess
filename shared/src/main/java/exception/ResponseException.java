package exception;

public class ResponseException extends Exception {
    final private String message;

    public ResponseException(String message) {
        super(message);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}