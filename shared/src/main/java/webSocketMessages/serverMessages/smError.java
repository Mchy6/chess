package webSocketMessages.serverMessages;

import chess.ChessGame;

public class smError extends ServerMessage {

    public smError(String errorMessage) {
        this.serverMessageType = ServerMessageType.ERROR;
//        this.setMessage(errorMessage);
        this.errorMessage = errorMessage;
    }

}