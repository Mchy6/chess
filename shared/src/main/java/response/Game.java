package response;

public class Game {
    private int gameID;
    private String whiteUsername;
    private String blackUsername;
    private String gameName;
    public Game(int gameID, String whiteUsername, String blackUsername, String gameName) {
        this.gameID = gameID;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.gameName = gameName;
    }
}
