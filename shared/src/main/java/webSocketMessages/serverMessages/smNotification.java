package webSocketMessages.serverMessages;

import chess.ChessGame;

public class smNotification extends ServerMessage {

    public smNotification(String message) {
        this.serverMessageType = ServerMessageType.NOTIFICATION;
        this.setMessage(message);
    }

    public ChessGame getGame() {
        return null;
    }
}