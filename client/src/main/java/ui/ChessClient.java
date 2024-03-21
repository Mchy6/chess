package ui;

import java.util.Arrays;

import com.google.gson.Gson;
import exception.ResponseException;
//import client.websocket.NotificationHandler;
import server.ServerFacade;
//import client.websocket.WebSocketFacade;
import static ui.EscapeSequences.*;

public class ChessClient {
    private String visitorName = null;
    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.SIGNEDOUT;

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
                case "signin" -> signIn(params);
//                case "rescue" -> rescuePet(params);
//                case "list" -> listPets();
//                case "signout" -> signOut();
//                case "adopt" -> adoptPet(params);
//                case "adoptall" -> adoptAllPets();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String signIn(String... params) throws ResponseException {
        if (params.length >= 1) {
            state = State.SIGNEDIN;
            visitorName = String.join("-", params);
//            ws = new WebSocketFacade(serverUrl, notificationHandler);
//            ws.enterPetShop(visitorName);
            return String.format("You signed in as %s.", visitorName);
        }
        throw new ResponseException("Expected: <yourname>");
    }

    public String signOut() throws ResponseException {
        assertSignedIn();
//        ws.leavePetShop(visitorName);
//        ws = null;
        state = State.SIGNEDOUT;
        return String.format("%s left the shop", visitorName);
    }

    public String help() {
        if (state == State.SIGNEDOUT) {
            return SET_TEXT_COLOR_BLUE + """
                    register <USERNAME> <PASSWORD> <EMAIL>""" + SET_TEXT_COLOR_LIGHT_GREY + " " + """
                    - to create an account""" + SET_TEXT_COLOR_BLUE + """
                    
                    login <USERNAME> <PASSWORD>""" + SET_TEXT_COLOR_LIGHT_GREY + " " + """
                    - to play chess""" + SET_TEXT_COLOR_BLUE + """
                    
                    quit""" + SET_TEXT_COLOR_LIGHT_GREY + " " + """
                    - playing chess""" + SET_TEXT_COLOR_BLUE + """
                    
                    help""" + SET_TEXT_COLOR_LIGHT_GREY + " " + """
                    - with possible commands""" + RESET_TEXT_COLOR;
        }
        // create, list, join, observe, logout, quit, help
        return """
                create <NAME>""" + SET_TEXT_COLOR_LIGHT_GREY + " " + """
                - a game""" + SET_TEXT_COLOR_BLUE + """
                
                list""" + SET_TEXT_COLOR_LIGHT_GREY + " " + """
                - games""" + SET_TEXT_COLOR_BLUE + """
                
                join <ID>""" + SET_TEXT_COLOR_LIGHT_GREY + " " + """
                - a game""" + SET_TEXT_COLOR_BLUE + """
                
                observe <ID>""" + SET_TEXT_COLOR_LIGHT_GREY + " " + """
                - a game""" + SET_TEXT_COLOR_BLUE + """
                
                logout""" + SET_TEXT_COLOR_LIGHT_GREY + " " + """
                - when you are done""" + SET_TEXT_COLOR_BLUE + """
                
                quit""" + SET_TEXT_COLOR_LIGHT_GREY + " " + """
                - playing chess""" + SET_TEXT_COLOR_BLUE + """
                
                help""" + SET_TEXT_COLOR_LIGHT_GREY + " " + """
                - with possible commands""" + RESET_TEXT_COLOR;
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.SIGNEDOUT) {
            throw new ResponseException("You must sign in");
        }
    }
}