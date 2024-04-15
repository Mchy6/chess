package webSocketMessages.serverMessages;

import chess.ChessGame;

public class SMNotification extends ServerMessage {

    public SMNotification(String message) {
        this.serverMessageType = ServerMessageType.NOTIFICATION;
        this.setMessage(message);
    }

    public ChessGame getGame() {
        return null;
    }
}