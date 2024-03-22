package response;

public class CreateGameResponse {
    private static int gameID;

    public CreateGameResponse(int gameID) {
        this.gameID = gameID;
    }
    public static int getGameID() {
        return gameID;
    }
}
