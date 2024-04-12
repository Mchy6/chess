package webSocketMessages.serverMessages;

import chess.ChessGame;

public class smError extends ServerMessage {

    public smError(String message) {
        this.serverMessageType = ServerMessageType.ERROR;
        this.setMessage(message);
    }

}