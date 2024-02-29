package exception;

public class AlreadyTakenException extends Exception {
    final private String message;
    public AlreadyTakenException(String message) {
        super(message);
        this.message = message;
    }
    public String getMessage() {
        return message;
    }

}
