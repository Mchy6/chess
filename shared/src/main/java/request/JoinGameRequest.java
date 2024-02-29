package request;

public class JoinGameRequest {
    private String authToken;
    private String playerColor;
    private int gameID;

    public JoinGameRequest (String playerColor, int gameID) {
        this.playerColor = playerColor;
        this.gameID = gameID;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getPlayerColor() {
        return playerColor;
    }
    public int getGameID() {
        return gameID;
    }
}
