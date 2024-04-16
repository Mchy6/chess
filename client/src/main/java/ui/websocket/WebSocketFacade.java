package ui.websocket;

import chess.*;
import com.google.gson.Gson;
import exception.ResponseException;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.*;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

//need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {

    Session session;
    ServerMessageHandler serverMessageHandler;


    public WebSocketFacade(String url, ServerMessageHandler serverMessageHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/connect");
            this.serverMessageHandler = serverMessageHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    System.out.println("Client received message: " + message);
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    System.out.println("Message after Gson: " + serverMessage.toString());
                    System.out.println("Game: " + serverMessage.getGame());
                    serverMessageHandler.notify(serverMessage);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(ex.getMessage());
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void joinPlayer(ChessGame.TeamColor playerColor, String authToken, int gameID) throws ResponseException {
        try {
            var ugc = new UGCJoinPlayer(playerColor, authToken, gameID);
            System.out.println("Sending message: " + new Gson().toJson(ugc));
            this.session.getBasicRemote().sendText(new Gson().toJson(ugc));

        } catch (IOException ex) {
            throw new ResponseException(ex.getMessage());
        }
    }
    public void joinObserver(String authToken, int gameID) throws ResponseException {
        try {
            var ugc = new UGCJoinObserver(authToken, gameID);
            System.out.println("Sending message: " + new Gson().toJson(ugc));
            this.session.getBasicRemote().sendText(new Gson().toJson(ugc));

        } catch (IOException ex) {
            throw new ResponseException(ex.getMessage());
        }
    }

    public void makeMove(String authToken, int gameID, ChessMove move) throws ResponseException {
        try {
            var ugc = new UGCMakeMove(authToken, gameID, move);
            System.out.println("Sending message: " + new Gson().toJson(ugc));
            this.session.getBasicRemote().sendText(new Gson().toJson(ugc));
        } catch (IOException ex) {
            throw new ResponseException(ex.getMessage());
        }
    }

    public void resign(String authToken, int gameID) throws ResponseException {
        try {
            int temp = 1;
            var ugc = new UGCResign(authToken, gameID);
            System.out.println("Sending message: " + new Gson().toJson(ugc));
            this.session.getBasicRemote().sendText(new Gson().toJson(ugc));

            if (temp == 5) {
                throw new ResponseException("We're no strangers to love");
            }
        } catch (IOException ex) {
            throw new ResponseException(ex.getMessage());
        }
    }

    public void leave(String authToken, int gameID) throws ResponseException {
        try {
            var ugc = new UGCLeave(authToken, gameID);
            System.out.println("Sending message: " + new Gson().toJson(ugc));
            this.session.getBasicRemote().sendText(new Gson().toJson(ugc));
        } catch (IOException ex) {
            throw new ResponseException(ex.getMessage());
        }
    }
}