package server.websocket;

import chess.*;
import com.google.gson.Gson;
//import dataaccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.MySqlDataAccess;
import exception.ResponseException;
import model.AuthData;
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
                joinPlayer(userGameCommand.getPlayerColor(), userGameCommand.getAuthToken(), session, userGameCommand.getID());
            }
            case JOIN_OBSERVER -> {
                joinObserver(userGameCommand.getAuthToken(), session, userGameCommand.getID());
            }
            case MAKE_MOVE -> {
                makeMove(userGameCommand.getID(), userGameCommand.getMove(), userGameCommand.getUsername());
            }
            case LEAVE -> { // must make ugcLeave
                leave(userGameCommand.getAuthToken(), userGameCommand.getUsername());
            }
        }
    }

//    private void joinPlayer(int gameID, ChessGame.TeamColor playerColor) {
//        System.out.println("gameID: " + gameID);
//        System.out.println("playerColor: " + playerColor);
//    }

    private void joinPlayer(ChessGame.TeamColor playerColor, String authToken, Session session, int gameID) throws IOException, InvalidMoveException, DataAccessException {
        connections.add(authToken, session);
        try {
            GameData gameData = dataAccess.getGame(gameID);
            AuthData authData = dataAccess.getAuthToken(authToken);
            if (authData == null) {
                connections.rootBroadcast(new smError("Error: invalid authToken"), authToken);
            } else {
                String playerName = authData.username();
                if (gameData == null) {
                    connections.rootBroadcast(new smError("Error: Game does not exist"), authToken);
                } else if (gameData.whiteUsername() == null || gameData.blackUsername() == null) {
                    connections.rootBroadcast(new smError("Error: HTTP not called, possibly"), authToken);
                } else if (playerColor != null && playerColor.equals(ChessGame.TeamColor.WHITE) && !(gameData.whiteUsername().equals(playerName))) {
                    connections.rootBroadcast(new smError("Error: trying to join as white player with wrong username"), authToken);
                } else if (playerColor != null && playerColor.equals(ChessGame.TeamColor.BLACK) && !(gameData.blackUsername().equals(playerName))) {
                    connections.rootBroadcast(new smError("Error: trying to join as black player with wrong username"), authToken);
                } else {

                    var notificationMessage = new smNotification(String.format("%s joined as the %s player", playerName, playerColor));
                    var loadGameMessage = new smLoadGame(gameData.game());
                    connections.rootBroadcast(loadGameMessage, authToken);
                    connections.excludeRootBroadcast(notificationMessage, authToken);
                }
            }

        } catch (DataAccessException e) {
            connections.broadcast(new smError("Error: " + e));
        }
    }

    private void joinObserver(String authToken, Session session, int gameID) throws IOException {
        connections.add(authToken, session);
        try {
            GameData gameData = dataAccess.getGame(gameID);
            AuthData authData = dataAccess.getAuthToken(authToken);
            if (authData == null) {
                connections.rootBroadcast(new smError("Error: invalid authToken"), authToken);
            } else {
                String playerName = authData.username();
                if (gameData == null) {
                    connections.rootBroadcast(new smError("Error: Game does not exist"), authToken);
                } else {
                    var notificationMessage = new smNotification(String.format("%s joined as an observer", playerName));
                    var loadGameMessage = new smLoadGame(gameData.game());
                    connections.rootBroadcast(loadGameMessage, authToken);
                    connections.excludeRootBroadcast(notificationMessage, authToken);
                }
            }

        } catch (DataAccessException e) {
            connections.broadcast(new smError("Error: " + e));
        }
    }

    // see: https://github.com/softwareconstruction240/softwareconstruction/blob/main/chess/6-gameplay/gameplay.md#notifications
    private void makeMove(int gameID, ChessMove move, String playerName) throws IOException { // need to add handling for errors
        // if not gameIsOver:
        try {
            dataAccess.getGame(gameID).game().makeMove(move);
//            var newGame = dataAccess.getGame(gameID).game().makeMove(move);
            // use updateGame to update the game in the database
            // get game and store
            // use makeMove on stored game
            // use updateGame with stored game

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
