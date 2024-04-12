package webSocketMessages.userCommands;

import chess.ChessGame;

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
    public String playerName;
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
    public String getPlayerName() {
        return playerName;
    }
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }
}
