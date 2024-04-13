package webSocketMessages.userCommands;

import chess.ChessGame;
import chess.ChessMove;

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
    public ChessGame.TeamColor teamColor;
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
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }
    public int getID() {
        return gameID;
    }
    public ChessMove getMove() {
        return move;
    }
}
