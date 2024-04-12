package webSocketMessages.serverMessages;

import com.google.gson.Gson;

public class ServerMessage {
    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    ServerMessageType serverMessageType;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}