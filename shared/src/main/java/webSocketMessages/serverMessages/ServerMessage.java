package webSocketMessages.serverMessages;

import chess.ChessGame;
import com.google.gson.Gson;

public class ServerMessage {

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    ServerMessageType serverMessageType;
    private ChessGame chessGame;
    private String message;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
    public ServerMessageType getServerMessageType() {
        return serverMessageType;
    }

    public String getMessage() {
        return message;
    }

    public ChessGame getGame() {
        return chessGame;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}