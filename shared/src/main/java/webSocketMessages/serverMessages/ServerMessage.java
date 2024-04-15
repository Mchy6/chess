package webSocketMessages.serverMessages;

import chess.ChessGame;
import com.google.gson.Gson;

import static webSocketMessages.serverMessages.SMLoadGame.drawChessBoard;

public class ServerMessage {

    public ChessGame game;

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    ServerMessageType serverMessageType;
    private ChessGame chessGame;
    private String message;
    public String errorMessage;
    private ChessGame.TeamColor playerColor;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
    public ServerMessageType getServerMessageType() {
        return serverMessageType;
    }

    public String getMessage() {
        if (message == null) {
            return errorMessage;
        }
        return message;
    }

    public ChessGame getGame() {
        return chessGame;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setPlayerColor(ChessGame.TeamColor playerColor) {
        this.playerColor = playerColor;
        this.setMessage(drawChessBoard(this.game, playerColor));
    }
}