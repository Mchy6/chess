package server.websocket;

import chess.*;
import com.google.gson.Gson;
//import dataaccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.MySqlDataAccess;
import exception.ResponseException;
import model.GameData;
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
    private MySqlDataAccess dataAccess = new MySqlDataAccess();

    public WebSocketHandler() throws DataAccessException {
    }
    // make new mysqldataaccess

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, InvalidMoveException, DataAccessException {
        System.out.println("Server received message: " + message);
        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        switch (userGameCommand.getType()) {
            case JOIN_PLAYER -> {
                joinPlayer(userGameCommand.getUsername(), userGameCommand.getTeamColor(), userGameCommand.getAuthToken(), session, userGameCommand.getID());
            }
            case JOIN_OBSERVER -> {
                joinObserver(userGameCommand.getUsername(), userGameCommand.getAuthToken(), session, userGameCommand.getID());
            }
            case MAKE_MOVE -> {
                makeMove(userGameCommand.getID(), userGameCommand.getMove(), userGameCommand.getUsername());
            }
            case LEAVE -> { // must make ugcLeave
                leave(userGameCommand.getAuthToken(), userGameCommand.getUsername());
            }
        }
    }

    private void joinPlayer(String playerName, ChessGame.TeamColor teamColor, String authToken, Session session, int gameID) throws IOException, InvalidMoveException, DataAccessException {
        // if there is an error: { connections.broadcast(new smError(error)); }

        try {
            GameData gameData = dataAccess.getGame(gameID);
            if (gameData == null) {
                connections.broadcast(new smError("Error: Game does not exist"));
            } else if (gameData.whiteUsername() != null && gameData.blackUsername() != null) {
                connections.broadcast(new smError("Error: Game is full"));
            } else if (gameData.whiteUsername() != null && teamColor.equals(ChessGame.TeamColor.WHITE)) {
                connections.broadcast(new smError("Error: White player is already taken"));
            } else if (gameData.blackUsername() != null && teamColor.equals(ChessGame.TeamColor.BLACK)) {
                connections.broadcast(new smError("Error: Black player is already taken"));
            } else {

                connections.add(authToken, session);
                var notificationMessage = new smNotification(String.format("%s joined as the %s player", playerName, teamColor));
                var loadGameMessage = new smLoadGame(gameData.game());
                connections.rootBroadcast(loadGameMessage, authToken);
                connections.excludeRootBroadcast(notificationMessage, authToken);
            }
        } catch (DataAccessException e) {
            connections.broadcast(new smError("Error: " + e));
        }
    }

    private void joinObserver(String observerName, String authToken, Session session, int gameID) throws IOException {


        try {
            GameData gameData = dataAccess.getGame(gameID);
            if (gameData == null) {
                connections.broadcast(new smError("Error: Game does not exist"));
            } else {
                connections.add(authToken, session);
                var notificationMessage = new smNotification(String.format("%s joined as an observer", observerName));
                connections.excludeRootBroadcast(notificationMessage, authToken);
                var loadGameMessage = new smLoadGame(gameData.game());
                connections.rootBroadcast(loadGameMessage, authToken);
            }
        } catch (DataAccessException e) {
            connections.broadcast(new smError("Error: " + e));
        }
    }

    // see: https://github.com/softwareconstruction240/softwareconstruction/blob/main/chess/6-gameplay/gameplay.md#notifications
    private void makeMove(int gameID, ChessMove move, String playerName) throws IOException { // need to add handling for errors
        try {
            dataAccess.getGame(gameID).game().makeMove(move);
        } catch (DataAccessException e) {
            connections.broadcast(new smError("Error: " + e));
        } catch (InvalidMoveException e) {
            throw new RuntimeException(e);
        }
    }

    private void resign(int gameID, String playerName, String authToken) throws IOException {
//        try {
////            dataAccess.getGame(gameID).game().endGame();
//
//        } catch (DataAccessException e) {
//            connections.broadcast(new smError("Error: " + e));
//        }

        var notificationMessage = new smNotification(String.format("%s resigned, game is over", playerName));
        connections.excludeRootBroadcast(notificationMessage, authToken);
        connections.remove(authToken);
    }

    private void leave(String authToken, String username) throws IOException {
        var notificationMessage = new smNotification(String.format("%s left the game", username));
        connections.excludeRootBroadcast(notificationMessage, authToken);
        connections.remove(authToken);

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
