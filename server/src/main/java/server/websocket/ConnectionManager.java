package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.serverMessages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, ConcurrentHashMap<String, Connection>> allGamesMap = new ConcurrentHashMap<>();


    public void add(String authToken, Session session, int gameID) {

        Connection connection = new Connection(authToken, session);
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
                c.send(serverMessage.toString());
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
                    c.send(serverMessage.toString());
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
                    c.send(serverMessage.toString());
                }
            }
        }
    }
}