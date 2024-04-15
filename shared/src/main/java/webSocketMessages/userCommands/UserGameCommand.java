package webSocketMessages.userCommands;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

public class UserGameCommand {



    public enum CommandType {
        JOIN_PLAYER,
        JOIN_OBSERVER,
        MAKE_MOVE,
        LEAVE,
        RESIGN
    }

    protected CommandType commandType;

    private String authToken;
    public String username;
    public int gameID;
    public ChessMove move;
    public ChessGame.TeamColor playerColor;
    public ChessPosition position;
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
    public CommandType getType() {
        return commandType;
    }

    public String getAuthToken() {
        return authToken;
    }
    public String getUsername() {
        return username;
    }
    public ChessGame.TeamColor getPlayerColor() {
        return playerColor;
    }
    public int getID() {
        return gameID;
    }
    public ChessMove getMove() {
        return move;
    }
    public ChessPosition getPosition() {
        return position;
    }
}
