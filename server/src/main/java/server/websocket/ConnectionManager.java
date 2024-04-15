package server.websocket;

import chess.ChessGame;
import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.serverMessages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static webSocketMessages.serverMessages.ServerMessage.ServerMessageType.LOAD_GAME;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, ConcurrentHashMap<String, Connection>> allGamesMap = new ConcurrentHashMap<>();


    public void add(String authToken, Session session, ChessGame.TeamColor playerColor) {
        int gameID = -1;

        for (ConcurrentHashMap.Entry<Integer, ConcurrentHashMap<String, Connection>> entry : allGamesMap.entrySet()) {
            ConcurrentHashMap<String, Connection> innerMap = entry.getValue();
            if (innerMap.containsKey(authToken)) {
                gameID = entry.getKey();
            }
        }

        Connection connection = new Connection(authToken, session, playerColor);
        ConcurrentHashMap<String, Connection> gameMap = allGamesMap.get(gameID);

        // If outerKey is not present, create a new innerMap and put it in outerMap
        if (gameMap == null) {
            gameMap = new ConcurrentHashMap<>();
            gameMap.put(authToken, connection);
            allGamesMap.put(gameID, gameMap);
        } else {
            // If outerKey is present, simply put the innerKey and connection into the existing innerMap
            gameMap.put(authToken, connection);
        }
    }

    public void remove(String authToken) {
        int gameID = -1;

        for (ConcurrentHashMap.Entry<Integer, ConcurrentHashMap<String, Connection>> entry : allGamesMap.entrySet()) {
            ConcurrentHashMap<String, Connection> innerMap = entry.getValue();
            if (innerMap.containsKey(authToken)) {
                gameID = entry.getKey();
            }
        }
        var connections = allGamesMap.get(gameID);

        connections.remove(authToken);

    }


    // do we care if the individual gets their own notification?
    // May need to modify to include game id so notifications only go to the correct game
    public void broadcast(ServerMessage serverMessage, String authToken) throws IOException {
        int gameID = -1;

        for (ConcurrentHashMap.Entry<Integer, ConcurrentHashMap<String, Connection>> entry : allGamesMap.entrySet()) {
            ConcurrentHashMap<String, Connection> innerMap = entry.getValue();
            if (innerMap.containsKey(authToken)) {
                gameID = entry.getKey();
            }
        }

        var connections = allGamesMap.get(gameID);
        var removeList = new ArrayList<Connection>();

        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (serverMessage.getServerMessageType() == LOAD_GAME) {
                    serverMessage.setPlayerColor(c.getTeamColor());
                } else {
                    c.send(serverMessage.toString());
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.remove(c.authToken);
        }
    }

    public void excludeRootBroadcast(ServerMessage serverMessage, String rootAuthToken) throws IOException {
        int gameID = -1;

        for (ConcurrentHashMap.Entry<Integer, ConcurrentHashMap<String, Connection>> entry : allGamesMap.entrySet()) {
            ConcurrentHashMap<String, Connection> innerMap = entry.getValue();
            if (innerMap.containsKey(rootAuthToken)) {
                gameID = entry.getKey();
            }
        }

        var connections = allGamesMap.get(gameID);
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (!c.authToken.equals(rootAuthToken)) {
                    if (serverMessage.getServerMessageType() == LOAD_GAME) {
                        serverMessage.setPlayerColor(c.getTeamColor());
                    } else {
                        c.send(serverMessage.toString());
                    }
                }
            }
        }
    }

    public void rootBroadcast(ServerMessage serverMessage, String rootAuthToken) throws IOException {
        int gameID = -1;

        for (ConcurrentHashMap.Entry<Integer, ConcurrentHashMap<String, Connection>> entry : allGamesMap.entrySet()) {
            ConcurrentHashMap<String, Connection> innerMap = entry.getValue();
            if (innerMap.containsKey(rootAuthToken)) {
                gameID = entry.getKey();
            }
        }

        var connections = allGamesMap.get(gameID);
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (c.authToken.equals(rootAuthToken)) {
                    if (serverMessage.getServerMessageType() == LOAD_GAME) {
                        serverMessage.setPlayerColor(c.getTeamColor());
                    } else {
                        c.send(serverMessage.toString());
                    }
                }
            }
        }
    }
}