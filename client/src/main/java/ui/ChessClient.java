package ui;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import exception.ResponseException;
//import client.websocket.NotificationHandler;
import server.ServerFacade;
//import client.websocket.WebSocketFacade;
import static ui.EscapeSequences.*;
import model.GameData;

public class ChessClient {
    private String username = null;
    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.LOGGEDOUT;
    private String authToken;


    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "create" -> create(params);
                case "list" -> list();
//                case "join" -> join(params);
//                case "observe" -> observe(params);
//                case "logout" -> logout();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String register(String ... params) throws ResponseException {
        if (params.length >= 3) {
            var username = params[0];
            var password = params[1];
            var email = params[2];
            var response = server.register(username, password, email);
            authToken = response.getAuthToken();
            state = State.LOGGEDIN;
            return String.format("Registered new user %s.", username);
        }
        throw new ResponseException("Expected: <USERNAME> <PASSWORD> <EMAIL>");
    }

    public String login(String ... params) throws ResponseException {
        if (params.length >= 2) {
            username = params[0];
            var password = params[1];
            var response = server.login(username, password);
            authToken = response.getAuthToken();
            System.out.println("authToken: " + authToken);
            state = State.LOGGEDIN;
            return String.format("Logged in as %s.", username);
        }
        throw new ResponseException("Expected: <USERNAME> <PASSWORD>");
    }

    public String create(String ... params) throws ResponseException {
        assertSignedIn();
        if (params.length >= 1) {
            var name = params[0];
            System.out.println("authToken: " + authToken);
            server.createGame(name, authToken);
            return String.format("Created game %s.", name);
        } else {
            throw new ResponseException("Expected: <NAME>");
        }
    }

    public String list() throws ResponseException {
        assertSignedIn();
        StringBuilder list = new StringBuilder();
        int index = 1;
        Collection<GameData> games = server.listGames(authToken).getGames();
        int totalGames = games.size();
        for (GameData game : games) {
            list.append(index).append(". ").append(game.gameName());
            if (index < totalGames)
                list.append("\n");
            index++;
        }
        return list.toString();
    }

    public String signOut() throws ResponseException {
        assertSignedIn();
//        ws.leavePetShop(visitorName);
//        ws = null;
//        state = State.SIGNEDOUT;
//        return String.format("%s left the shop", visitorName);
        return null;
    }

    public String help() {
        if (state == State.LOGGEDOUT) {
            return SET_TEXT_COLOR_BLUE + "  " + """
                    register <USERNAME> <PASSWORD> <EMAIL>""" + SET_TEXT_COLOR_LIGHT_GREY + " " + """
                    - to create an account""" + SET_TEXT_COLOR_BLUE + "\n  " + """
                    login <USERNAME> <PASSWORD>""" + SET_TEXT_COLOR_LIGHT_GREY + " " + """
                    - to play chess""" + SET_TEXT_COLOR_BLUE + "\n  " + """
                    quit""" + SET_TEXT_COLOR_LIGHT_GREY + " " + """
                    - playing chess""" + SET_TEXT_COLOR_BLUE + "\n  " + """
                    help""" + SET_TEXT_COLOR_LIGHT_GREY + " " + """
                    - with possible commands""" + RESET_TEXT_COLOR;
        }
        return SET_TEXT_COLOR_BLUE + "  " + """
                create <NAME>""" + SET_TEXT_COLOR_LIGHT_GREY + " " + """
                - a game""" + SET_TEXT_COLOR_BLUE + "\n  " + """
                list""" + SET_TEXT_COLOR_LIGHT_GREY + " " + """
                - games""" + SET_TEXT_COLOR_BLUE + "\n  " + """
                join <ID>""" + SET_TEXT_COLOR_LIGHT_GREY + " " + """
                - a game""" + SET_TEXT_COLOR_BLUE + "\n  " + """
                observe <ID>""" + SET_TEXT_COLOR_LIGHT_GREY + " " + """
                - a game""" + SET_TEXT_COLOR_BLUE + "\n  " + """
                logout""" + SET_TEXT_COLOR_LIGHT_GREY + " " + """
                - when you are done""" + SET_TEXT_COLOR_BLUE + "\n  " + """
                quit""" + SET_TEXT_COLOR_LIGHT_GREY + " " + """
                - playing chess""" + SET_TEXT_COLOR_BLUE + "\n  " + """
                help""" + SET_TEXT_COLOR_LIGHT_GREY + " " + """
                - with possible commands""" + RESET_TEXT_COLOR;
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.LOGGEDOUT) {
            throw new ResponseException("You must log in");
        }
    }
}