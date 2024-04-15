package server.websocket;

import chess.*;
import com.google.gson.Gson;
//import dataaccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.MySqlDataAccess;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.serverMessages.smError;
import webSocketMessages.serverMessages.smLoadGame;
import webSocketMessages.serverMessages.smNotification;
import webSocketMessages.userCommands.UserGameCommand;

import java.io.IOException;


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
                makeMove(userGameCommand.getAuthToken(), userGameCommand.getID(), userGameCommand.getMove());
            }
            case RESIGN -> {
                resign(userGameCommand.getAuthToken(), userGameCommand.getID());
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
                } else if (gameData.whiteUsername() == null && gameData.blackUsername() == null) {
                    connections.rootBroadcast(new smError("Error: HTTP not called, possibly"), authToken);
                } else if (playerColor != null && playerColor.equals(ChessGame.TeamColor.WHITE) && !(gameData.whiteUsername().equals(playerName))) {
                    connections.rootBroadcast(new smError("Error: trying to join as white player with wrong username"), authToken);
                } else if (playerColor != null && playerColor.equals(ChessGame.TeamColor.BLACK) && !(gameData.blackUsername().equals(playerName))) {
                    connections.rootBroadcast(new smError("Error: trying to join as black player with wrong username"), authToken);
                } else {

                    var notificationMessage = new smNotification(String.format("%s joined as the %s player", playerName, playerColor));
                    var loadGameMessage = new smLoadGame(gameData.game(), playerColor, null);
                    connections.rootBroadcast(loadGameMessage, authToken);
                    connections.excludeRootBroadcast(notificationMessage, authToken);
                }
            }

        } catch (DataAccessException e) {
            connections.broadcast(new smError("Error thrown: " + e), authToken);
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
                String observerName = authData.username();
                if (gameData == null) {
                    connections.rootBroadcast(new smError("Error: Game does not exist"), authToken);
                } else {
                    var notificationMessage = new smNotification(String.format("%s joined as an observer", observerName));
                    var loadGameMessage = new smLoadGame(gameData.game(), ChessGame.TeamColor.WHITE, null);
                    connections.rootBroadcast(loadGameMessage, authToken);
                    connections.excludeRootBroadcast(notificationMessage, authToken);
                }
            }

        } catch (DataAccessException e) {
            connections.broadcast(new smError("Error thrown: " + e), authToken);
        }
    }

    // see: https://github.com/softwareconstruction240/softwareconstruction/blob/main/chess/6-gameplay/gameplay.md#notifications
    private void makeMove(String authToken, int gameID, ChessMove move) throws IOException { // need to add handling for errors
        // if not gameIsOver:
        try {
            GameData gameData = dataAccess.getGame(gameID);
            AuthData authData = dataAccess.getAuthToken(authToken);
            String playerName = authData.username();

            ChessGame game = gameData.game();

            System.out.println("DEBUG: is game over in makeMove? " + game.isGameOver());


            // Check player is not out of turn
            if (game.getTeamTurn() == ChessGame.TeamColor.WHITE && !gameData.whiteUsername().equals(playerName)) {
                connections.rootBroadcast(new smError("Error: not white player's turn"), authToken);
                return;
            } else if (game.getTeamTurn() == ChessGame.TeamColor.BLACK && !gameData.blackUsername().equals(playerName)) {
                connections.rootBroadcast(new smError("Error: not black player's turn"), authToken);
                return;
            } else if (game.isGameOver()) {
                connections.rootBroadcast(new smError("Error: Cannot make move, game is over"), authToken);
                return;
            } else {
                // Server verifies the validity of the move.
                if (!gameData.game().validMoves(move.getStartPosition()).contains(move)) {
                    connections.rootBroadcast(new smError("Error: invalid move"), authToken);
                    return;
                }

                game.makeMove(move);
//                game.setTeamTurn(game.getTeamTurn() == ChessGame.TeamColor.WHITE ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE);

                GameData updatedGameData = new GameData(gameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);

                dataAccess.updateGame(updatedGameData);

                ChessPosition startPosition = move.getStartPosition();
                ChessPosition endPosition = move.getEndPosition();

                var notificationMessage = new smNotification(String.format("%s moved %s to %s", playerName, startPosition, endPosition));
                connections.excludeRootBroadcast(notificationMessage, authToken);

                var loadGameMessageRoot = new smLoadGame(game, game.getTeamTurn(), null);
                connections.rootBroadcast(loadGameMessageRoot, authToken);
                var loadGameMessageOpponent = new smLoadGame(game, game.getTeamTurn() == ChessGame.TeamColor.WHITE ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE, null);
                connections.excludeRootBroadcast(loadGameMessageOpponent, authToken);
//                var loadGameMessageObserver = new smLoadGame(game, ChessGame.TeamColor.WHITE);
//                connections.observerBroadcast(loadGameMessageObserver);

                // If the move results in check or checkmate the server sends a Notification message to all clients.

                if (game.isInCheckmate(game.getTeamTurn())) {
                    var checkmateMessage = new smNotification(String.format("%s is in checkmate, game is over", playerName));
                    connections.excludeRootBroadcast(checkmateMessage, authToken);
                } else if (game.isInStalemate(game.getTeamTurn())) {
                    var stalemateMessage = new smNotification(String.format("%s is in stalemate, game is over", playerName));
                    connections.excludeRootBroadcast(stalemateMessage, authToken);
                } else if (game.isInCheck(game.getTeamTurn())) {
                    var checkMessage = new smNotification(String.format("%s is in check", playerName));
                    connections.excludeRootBroadcast(checkMessage, authToken);
                }

            }

        } catch (DataAccessException e) {
            connections.broadcast(new smError("Error thrown: " + e), authToken);
        } catch (InvalidMoveException e) {
            throw new RuntimeException(e);
        }
    }

    private void resign(String authToken, int gameID) throws IOException {
        try {
            GameData gameData = dataAccess.getGame(gameID);
            AuthData authData = dataAccess.getAuthToken(authToken);
            String playerName = authData.username();
            ChessGame game = gameData.game();

            if (!(gameData.whiteUsername().equals(playerName) || gameData.blackUsername().equals(playerName))) {
                connections.rootBroadcast(new smError("Error: observer cannot resign"), authToken);
                return;
            } else if (game.isGameOver()) {
                connections.rootBroadcast(new smError("Error: Cannot resign, game is over"), authToken);
                return;
            }
            System.out.println("DEBUG: is game over in resign? " + gameData.game().isGameOver());

            game.setGameIsOverTrue();
            GameData updatedGameData = new GameData(gameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);

            System.out.println("DEBUG: is game over in resign? " + updatedGameData.game().isGameOver());

            dataAccess.updateGame(updatedGameData);
            var notificationMessage = new smNotification(String.format("%s resigned, game is over", playerName));
            connections.broadcast(notificationMessage, authToken);
            connections.remove(authToken);
        } catch (DataAccessException e) {
            connections.broadcast(new smError("Error thrown: " + e), authToken);
        }

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
