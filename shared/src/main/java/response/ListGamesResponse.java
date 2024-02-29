package response;
import model.GameData;
import java.util.Collection;

public class ListGamesResponse {
    private String message;
    private Collection<GameData> games;

    public ListGamesResponse(Collection<GameData> games) {
        this.games = games;
    }
    public ListGamesResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public Collection<GameData> getGames() {
        return games;
    }
}


