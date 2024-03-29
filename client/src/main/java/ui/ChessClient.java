package ui;

import java.lang.reflect.Array;
import java.util.*;

import exception.ResponseException;
//import client.websocket.NotificationHandler;
import response.CreateGameResponse;
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

    private Collection<GameData> gameCollection;

    private Map<Integer, Integer> gameMap;


    public ChessClient(String serverUrl) throws ResponseException {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        server.clearDatabase();
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
                case "join" -> join(params);
                case "observe" -> observe(params);
                case "logout" -> logout();
                case "quit" -> quit();
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
            state = State.LOGGEDIN;
            return String.format("Logged in as %s.", username);
        }

        throw new ResponseException("Expected: <USERNAME> <PASSWORD>");
    }

    public String logout(String ... params) throws ResponseException {
        if (params.length == 0) {
            server.logout(authToken);
            authToken = null;
            state = State.LOGGEDOUT;
            return String.format("Logged out %s.", username);
        }
        throw new ResponseException("Expected: no arguments");
    }

    public String create(String ... params) throws ResponseException {
        assertSignedIn();
        if (params.length >= 1) {
            var name = params[0];
            CreateGameResponse createGameResponse = server.createGame(name, authToken);
            return String.format("Created game %s.", name);
        } else {
            throw new ResponseException("Expected: <NAME>");
        }
    }

    public String list() throws ResponseException {
        gameMap = new HashMap<>();
        gameCollection = new ArrayList<>();
        assertSignedIn();
        StringBuilder list = new StringBuilder();
        int index = 1;
        gameCollection = server.listGames(authToken).getGames();
        int totalGames = gameCollection.size();
        for (GameData game : gameCollection) {
//            System.out.println("DEBUG: from list, white and black " + game.whiteUsername() + " " + game.blackUsername());
            gameMap.put(index, game.gameID());
            list.append(index).append(". ").append(game.gameName());
            if (game.whiteUsername() == null) {
                list.append(" white player: no player |");
            } else {
                list.append(" white player: ").append(game.whiteUsername()).append(" |");
            }
            if (game.blackUsername() == null) {
                list.append(" black: no player");
            } else {
                list.append(" black: ").append(game.blackUsername());
            }

            if (index < totalGames)
                list.append("\n");
            index++;
        }
        return list.toString();
    }

    public String join(String ... params) throws ResponseException {
        assertSignedIn();
        if (params.length >= 2) {
            var number = Integer.parseInt(params[0]);
            var color = params[1];
            var id = gameMap.get(number);
            var game = gameCollection.stream().filter(g -> g.gameID() == id).findFirst().orElse(null);
//            System.out.println("DEBUG: from join, white and black " + game.whiteUsername() + " " + game.blackUsername());
            if (game.whiteUsername() != null && game.blackUsername() != null) {
                throw new ResponseException("Game is full");
            } else if (game.whiteUsername() != null && color.equalsIgnoreCase("white")) {
                throw new ResponseException("White player is already taken");
            } else if (game.blackUsername() != null && color.equalsIgnoreCase("black")) {
                throw new ResponseException("Black player is already taken");
            } else if (game.whiteUsername() == null && color.equalsIgnoreCase("white")) {

            }
            server.joinGame(color, id, authToken);
            StringBuilder result = new StringBuilder();
            result.append("Joined game ").append(" as the ").append(color).append(" player.");
            result.append(createStartingBoard());
            return result.toString();
        }
        throw new ResponseException("Expected: <ID> <PLAYER_COLOR>");
    }

    public String observe(String ... params) throws ResponseException {
        assertSignedIn();
        if (params.length >= 1) {
            var number = Integer.parseInt(params[0]);
            var id = gameMap.get(number);
            StringBuilder result = new StringBuilder();
            result.append("Observing game ").append(".");
            result.append(createStartingBoard());
            return result.toString();
        }
        throw new ResponseException("Expected: <ID>");
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
                join <ID> <PLAYER_COLOR>""" + SET_TEXT_COLOR_LIGHT_GREY + " " + """
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

    public static String createStartingBoard() {
        StringBuilder board = new StringBuilder();
        String l = SET_BG_COLOR_LIGHT_GREY;
        String d = SET_BG_COLOR_DARK_GREY;
        String r = SET_TEXT_COLOR_RED;
        String b = SET_TEXT_COLOR_BLUE;
        String w = SET_TEXT_COLOR_WHITE;
        String c = RESET_BG_COLOR;

        // board 1:
        board.append(w).append(c).append("\n    h  g  f  e  d  c  b  a\n");
        board.append(" 1 ").append(r).append(l).append(" R ").append(d).append(" N ").append(l).append(" B ").append(d).append(" K ").append(l).append(" Q ").append(d).append(" B ").append(l).append(" N ").append(d).append(" R ").append(c).append(w).append(" 1 ").append("\n");
        board.append(" 2 ").append(r).append(d).append(" P ").append(l).append(" P ").append(d).append(" P ").append(l).append(" P ").append(d).append(" P ").append(l).append(" P ").append(d).append(" P ").append(l).append(" P ").append(c).append(w).append(" 2 ").append("\n");

        for (int i = 2; i < 6; i+=2) {
            board.append(" ").append(i).append(" ").append(r).append(l).append("   ").append(d).append("   ").append(l).append("   ").append(d).append("   ").append(l).append("   ").append(d).append("   ").append(l).append("   ").append(d).append("   ").append(c).append(w).append(" ").append(i).append(" ").append("\n");
            board.append(" ").append(i+1).append(" ").append(r).append(d).append("   ").append(l).append("   ").append(d).append("   ").append(l).append("   ").append(d).append("   ").append(l).append("   ").append(d).append("   ").append(l).append("   ").append(c).append(w).append(" ").append(i+1).append(" ").append("\n");
        }
        board.append(" 7 ").append(b).append(l).append(" P ").append(d).append(" P ").append(l).append(" P ").append(d).append(" P ").append(l).append(" P ").append(d).append(" P ").append(l).append(" P ").append(d).append(" P ").append(c).append(w).append(" 7 ").append("\n");
        board.append(" 8 ").append(b).append(d).append(" R ").append(l).append(" N ").append(d).append(" B ").append(l).append(" K ").append(d).append(" Q ").append(l).append(" B ").append(d).append(" N ").append(l).append(" R ").append(c).append(w).append(" 8 ").append("\n");
        board.append(w).append(c).append("    h  g  f  e  d  c  b  a\n");

        board.append("\n");

        // board 2:
        board.append(w).append(c).append("    a  b  c  d  e  f  g  h\n");
        board.append(" 8 ").append(b).append(l).append(" R ").append(d).append(" N ").append(l).append(" B ").append(d).append(" K ").append(l).append(" Q ").append(d).append(" B ").append(l).append(" N ").append(d).append(" R ").append(c).append(w).append(" 8 ").append("\n");
        board.append(" 7 ").append(b).append(d).append(" P ").append(l).append(" P ").append(d).append(" P ").append(l).append(" P ").append(d).append(" P ").append(l).append(" P ").append(d).append(" P ").append(l).append(" P ").append(c).append(w).append(" 7 ").append("\n");

        for (int i = 6; i > 2; i-=2) {
            board.append(" ").append(i).append(" ").append(r).append(l).append("   ").append(d).append("   ").append(l).append("   ").append(d).append("   ").append(l).append("   ").append(d).append("   ").append(l).append("   ").append(d).append("   ").append(c).append(w).append(" ").append(i).append(" ").append("\n");
            board.append(" ").append(i-1).append(" ").append(r).append(d).append("   ").append(l).append("   ").append(d).append("   ").append(l).append("   ").append(d).append("   ").append(l).append("   ").append(d).append("   ").append(l).append("   ").append(c).append(w).append(" ").append(i-1).append(" ").append("\n");
        }
        board.append(" 2 ").append(r).append(l).append(" P ").append(d).append(" P ").append(l).append(" P ").append(d).append(" P ").append(l).append(" P ").append(d).append(" P ").append(l).append(" P ").append(d).append(" P ").append(c).append(w).append(" 2 ").append("\n");
        board.append(" 1 ").append(r).append(d).append(" R ").append(l).append(" N ").append(d).append(" B ").append(l).append(" K ").append(d).append(" Q ").append(l).append(" B ").append(d).append(" N ").append(l).append(" R ").append(c).append(w).append(" 1 ").append("\n");
        board.append("    a  b  c  d  e  f  g  h\n");


        return board.toString();
    }

    public static String quit() {
        System.exit(0);
        return "quit";
    }

}

// fails when 2 players join same game as same color
// board should rotate 180
// list games should show username of white and black player
// make sure quit shuts down program