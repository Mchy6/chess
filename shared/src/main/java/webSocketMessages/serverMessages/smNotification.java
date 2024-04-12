package webSocketMessages.serverMessages;

public class smNotification extends ServerMessage {
    private final String message;

    public smNotification(String message) {
        this.serverMessageType = ServerMessageType.NOTIFICATION;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}