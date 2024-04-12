package server.websocket;

import chess.*;
import com.google.gson.Gson;
//import dataaccess.DataAccess;
import exception.ResponseException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.serverMessages.smError;
import webSocketMessages.serverMessages.smLoadGame;
import webSocketMessages.serverMessages.smNotification;
import webSocketMessages.userCommands.UserGameCommand;
import webSocketMessages.userCommands.ugcJoinPlayer;

import java.io.IOException;
import java.util.Timer;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, InvalidMoveException {
        System.out.println("Server recieved message: " + message);
        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        switch (userGameCommand.getType()) {
            case JOIN_PLAYER -> {
                joinPlayer(userGameCommand.getUsername(), userGameCommand.getTeamColor(), userGameCommand.getAuthToken(), session);
            }
            case JOIN_OBSERVER -> {
                //joinObserver(userGameCommand.getUsername(), userGameCommand.getAuthToken(), session);
            }
        }
    }

    private void joinPlayer(String playerName, ChessGame.TeamColor teamColor, String authToken, Session session) throws IOException, InvalidMoveException {
        // if there is an error: { connections.broadcast(new smError(error)); }

        connections.add(authToken, session);
        var notificationMessage = new smNotification(String.format("%s joined as the %s player", playerName, teamColor));
        var loadGameMessage = new smLoadGame(new ChessGame()); // need to pass in the game, right now just sends a new game
        connections.rootBroadcast(loadGameMessage, authToken);
        connections.excludeRootBroadcast(notificationMessage, authToken);
    }

    

//    private void exit(String visitorName) throws IOException {
//        connections.remove(visitorName);
//        var message = String.format("%s left the shop", visitorName);
//        var notification = new Notification(Notification.Type.DEPARTURE, message);
//        connections.broadcast(visitorName, notification);
//    }
//
//    public void makeNoise(String petName, String sound) throws ResponseException {
//        try {
//            var message = String.format("%s says %s", petName, sound);
//            var notification = new Notification(Notification.Type.NOISE, message);
//            connections.broadcast("", notification);
//        } catch (Exception ex) {
//            throw new ResponseException(500, ex.getMessage());
//        }
//    }
}
