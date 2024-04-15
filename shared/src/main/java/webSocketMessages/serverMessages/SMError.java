package webSocketMessages.serverMessages;

public class SMError extends ServerMessage {

    public SMError(String errorMessage) {
        this.serverMessageType = ServerMessageType.ERROR;
//        this.setMessage(errorMessage);
        this.errorMessage = errorMessage;
    }

}