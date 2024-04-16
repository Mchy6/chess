//makemove (2,a) (3,a)

package ui;

import java.lang.reflect.Array;
import java.util.*;

import chess.*;
import exception.ResponseException;
import ui.websocket.ServerMessageHandler;
import response.CreateGameResponse;
import server.ServerFacade;
import ui.websocket.WebSocketFacade;
import static ui.EscapeSequences.*;
import model.GameData;
import ui.websocket.ServerMessageHandler;
import ui.websocket.WebSocketFacade;

public class ChessClient {
    private String username = null;
    private ChessGame.TeamColor playerColor;
    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.LOGGEDOUT;
    private String authToken;
    private final ServerMessageHandler serverMessageHandler;
    private WebSocketFacade ws;
    private Collection<GameData> gameCollection;

    private Map<Integer, Integer> gameMap;
    private int gameID; // may not be updating as often as necessary
    private static ChessGame currentGame;

    public ChessClient(String serverUrl, ServerMessageHandler serverMessageHandler) throws ResponseException {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.serverMessageHandler = serverMessageHandler;
        ws = new WebSocketFacade(serverUrl, serverMessageHandler);

//        server.clearDatabase();
    }

    public ChessGame.TeamColor getPlayerColor() { // set for join or observe
        return playerColor;
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
                case "redraw" -> redraw();
                case "leave" -> leave();
                case "makemove" -> makeMove(params);
                case "resign" -> resign();
                case "highlight" -> highlight(params);
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

            // for websocket
            String error = "no error";

            if (color.equalsIgnoreCase("white")) {
                playerColor = ChessGame.TeamColor.WHITE;
            } else if (color.equalsIgnoreCase("black")) {
                playerColor = ChessGame.TeamColor.BLACK;
            }


            if (game.whiteUsername() != null && game.blackUsername() != null) {
                error = "Game is full";
                throw new ResponseException(error);
            } else if (game.whiteUsername() != null && color.equalsIgnoreCase("white")) {
                error = "White player is already taken";
                throw new ResponseException(error);
            } else if (game.blackUsername() != null && color.equalsIgnoreCase("black")) {
                error = "Black player is already taken";
                throw new ResponseException(error);
            } else if (game.whiteUsername() == null && color.equalsIgnoreCase("white")) {

            }

            server.joinGame(color, id, authToken);
            StringBuilder result = new StringBuilder();
            result.append("Joined game").append(" as the ").append(color).append(" player.");

            ws.joinPlayer(playerColor, authToken, id);
            this.gameID = id;
            state = State.PLAYINGGAME;

            currentGame = game.game();
            return result.toString();
        }
        throw new ResponseException("Expected: <ID> <PLAYER_COLOR>");
    }

    public String observe(String ... params) throws ResponseException {
        assertSignedIn();
        if (params.length >= 1) {
            var number = Integer.parseInt(params[0]);
            var id = gameMap.get(number);
            var game = gameCollection.stream().filter(g -> g.gameID() == id).findFirst().orElse(null);
            StringBuilder result = new StringBuilder();
            result.append("Observing game ").append(".");
            state = State.OBSERVINGGAME;

            this.playerColor = null;

            ws.joinObserver(authToken, id);
            this.gameID = id;

            assert game != null;
            currentGame = game.game();

            return result.toString();
        }
        throw new ResponseException("Expected: <ID>");
    }

    public String makeMove(String ... params) throws ResponseException {
        assertSignedIn();
        if (params.length >= 2) {
            var start = params[0];
            var end = params[1];
            var move = stringToChessMove(start, end);
            var game = gameCollection.stream().filter(g -> g.gameID() == gameID).findFirst().orElse(null);
            System.out.println("ChessMove move: " + move);
            assert game != null;
            currentGame = game.game();
            ws.makeMove(authToken, gameID, move);
        } else {
            throw new ResponseException("Expected: <STARTING_COORDINATES> <ENDING_COORDINATES>");
        }
        return "";
    }

    public String highlight(String ... params) throws ResponseException {
        assertSignedIn();
        if (params.length >= 1) {
            var position = params[0];
            var game = gameCollection.stream().filter(g -> g.gameID() == gameID).findFirst().orElse(null);
            String removeParenthesis = position.replaceAll("[()]", "");

            // Split the strings into the x and y coordinates
            String[] coordinates = removeParenthesis.split(",");

            int xCoordinate = Integer.parseInt(coordinates[0]);
            int yCoordinate = (int) coordinates[1].charAt(0) - 'a' + 1;

            var pos = new ChessPosition(xCoordinate, yCoordinate);
            assert game != null;
            return drawHighlight(game.game(), pos, playerColor);

        } else {
            throw new ResponseException("Expected: <PIECE_COORDINATES>");
        }
    }

    public String redraw() throws ResponseException {
        assertSignedIn();
        var game = gameCollection.stream().filter(g -> g.gameID() == gameID).findFirst().orElse(null);
        assert game != null;
        return drawChessBoard(game.game(), playerColor);
    }

    public String resign() throws ResponseException {
        assertSignedIn();
        ws.resign(authToken, gameID);
        authToken = null;
        state = State.LOGGEDIN;
        return "";

    }

    public String leave() throws ResponseException {
        assertSignedIn();
        ws.leave(authToken, gameID);
        authToken = null;
        state = State.LOGGEDIN;
        return "";
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
        } else if (state == State.LOGGEDIN) {
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
        } else if (state == State.PLAYINGGAME) {
            return SET_TEXT_COLOR_BLUE + "  " + """
                    help""" + SET_TEXT_COLOR_LIGHT_GREY + " " + """
                    - with possible commands""" + SET_TEXT_COLOR_BLUE + "\n  " + """
                    redraw""" + SET_TEXT_COLOR_LIGHT_GREY + " " + """
                    - the chess board""" + SET_TEXT_COLOR_BLUE + "\n  " + """
                    leave""" + SET_TEXT_COLOR_LIGHT_GREY + " " + """
                    - the game""" + SET_TEXT_COLOR_BLUE + "\n  " + """
                    makeMove <STARTING_COORDINATES> <ENDING_COORDINATES>""" + SET_TEXT_COLOR_LIGHT_GREY + " " + """
                    - to move a piece""" + SET_TEXT_COLOR_BLUE + "\n  " + """
                    resign""" + SET_TEXT_COLOR_LIGHT_GREY + " " + """
                    - the game""" + SET_TEXT_COLOR_BLUE + "\n  " + """
                    highlight <PIECE_COORDINATES>""" + SET_TEXT_COLOR_LIGHT_GREY + " " + """
                    - for a piece""" + RESET_TEXT_COLOR;
        }
        return SET_TEXT_COLOR_BLUE + "  " + """
                help""" + SET_TEXT_COLOR_LIGHT_GREY + " " + """
                - with possible commands""" + SET_TEXT_COLOR_BLUE + "\n  " + """
                redraw""" + SET_TEXT_COLOR_LIGHT_GREY + " " + """
                - the chess board""" + SET_TEXT_COLOR_BLUE + "\n  " + """
                leave""" + SET_TEXT_COLOR_LIGHT_GREY + " " + """
                - the game""" + SET_TEXT_COLOR_BLUE + "\n  " + """
                highlight <PIECE_COORDINATES>""" + SET_TEXT_COLOR_LIGHT_GREY + " " + """
                - for a piece""" + RESET_TEXT_COLOR;
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.LOGGEDOUT) {
            throw new ResponseException("You must log in");
        }
    }

    public static String quit() {
        System.exit(0);
        return "quit";
    }
    private static ChessMove stringToChessMove(String startString, String endString) {
        // Remove the surrounding parentheses
        startString = startString.replaceAll("[()]", "");
        endString = endString.replaceAll("[()]", "");

        // Split the strings into the x and y coordinates
        String[] startCoords = startString.split(",");
        String[] endCoords = endString.split(",");

        // Extract the x and y coordinates
        int startX = Integer.parseInt(startCoords[0]);
        int startY = (int) startCoords[1].charAt(0) - 'a' + 1;
        int endX = Integer.parseInt(endCoords[0]);
        int endY = (int) endCoords[1].charAt(0) - 'a' + 1;

        // Create ChessPosition objects for the start and end positions
        ChessPosition start = new ChessPosition(startX, startY);
        ChessPosition end = new ChessPosition(endX, endY);

        // Create a new ChessMove object
        return new ChessMove(start, end, null);
    }

    public static String drawHighlight(ChessGame game, ChessPosition position, ChessGame.TeamColor teamColor) {
        ChessBoard board = game.getBoard();
        Collection<ChessMove> moves = game.validMoves(position);

        System.out.println("drawChessBoard called");
        StringBuilder sb = new StringBuilder();
        String l = SET_BG_COLOR_LIGHT_GREY;
        String d = SET_BG_COLOR_DARK_GREY;
        String r = SET_TEXT_COLOR_RED;
        String b = SET_TEXT_COLOR_BLUE;
        String w = SET_TEXT_COLOR_WHITE;
        String c = RESET_BG_COLOR;
        String h = SET_BG_COLOR_GREEN; // Highlight color

        if (teamColor == ChessGame.TeamColor.WHITE) {
            sb.append(w).append(c).append("\n    h  g  f  e  d  c  b  a\n");
        } else {
            sb.append(w).append(c).append("\n    a  b  c  d  e  f  g  h\n");
        }

        int startRow = teamColor == ChessGame.TeamColor.BLACK ? 8 : 1;
        int endRow = teamColor == ChessGame.TeamColor.BLACK ? 0 : 9;
        int rowStep = teamColor == ChessGame.TeamColor.BLACK ? -1 : 1;

        for (int row = startRow; row != endRow; row += rowStep) {
            sb.append(" ").append(Math.abs(row)).append(" ");
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                boolean isHighlighted = false;
                for (ChessMove move : moves) {
                    if (move.getEndPosition().equals(pos)) {
                        isHighlighted = true;
                        break;
                    }
                }
                if (piece == null) {
                    sb.append(r).append((row + col) % 2 == 0 ? l : d)
                            .append(isHighlighted ? h+"   " : "   ")
                            .append(c).append(w);
                } else {
                    char pieceChar = getPieceSymbol(piece);
                    sb.append(piece.getTeamColor() == ChessGame.TeamColor.BLACK ? b : r)
                            .append((row + col) % 2 == 0 ? l : d)
                            .append(isHighlighted ? h+"   " : " ")
                            .append(pieceChar)
                            .append(isHighlighted ? h+"   " : " ")
                            .append(c).append(w);
                }
            }
            sb.append(" ").append(Math.abs(row)).append("\n");
        }

        // Add the last row based on the teamColor
        if (teamColor == ChessGame.TeamColor.WHITE) {
            sb.append(w).append(c).append("    h  g  f  e  d  c  b  a\n");
        } else {
            sb.append(w).append(c).append("    a  b  c  d  e  f  g  h\n");
        }
        return sb.toString();
    }

    public static String drawChessBoard(ChessGame game, ChessGame.TeamColor teamColor) {
        ChessBoard board = game.getBoard();
        ChessGame.TeamColor currentTeamColor = teamColor;

        System.out.println("drawChessBoard called");
        StringBuilder sb = new StringBuilder();
        String l = SET_BG_COLOR_LIGHT_GREY;
        String d = SET_BG_COLOR_DARK_GREY;
        String r = SET_TEXT_COLOR_RED;
        String b = SET_TEXT_COLOR_BLUE;
        String w = SET_TEXT_COLOR_WHITE;
        String c = RESET_BG_COLOR;

        // Add the first row based on the teamColor
        if (teamColor == ChessGame.TeamColor.WHITE) {
            sb.append(w).append(c).append("\n    h  g  f  e  d  c  b  a\n");
        } else {
            sb.append(w).append(c).append("\n    a  b  c  d  e  f  g  h\n");
        }

        int startRow = currentTeamColor == ChessGame.TeamColor.BLACK ? 8 : 1;
        int endRow = currentTeamColor == ChessGame.TeamColor.BLACK ? 0 : 9;
        int rowStep = currentTeamColor == ChessGame.TeamColor.BLACK ? -1 : 1;

        for (int row = startRow; row != endRow; row += rowStep) {
            sb.append(" ").append(row).append(" ");
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if (piece == null) {
                    sb.append(r).append((row + col) % 2 == 0 ? l : d).append("   ").append(c).append(w);
                } else {
                    char pieceChar = getPieceSymbol(piece);
                    sb.append(piece.getTeamColor() == ChessGame.TeamColor.BLACK ? b : r)
                            .append((row + col) % 2 == 0 ? l : d)
                            .append(" ").append(pieceChar).append(" ")
                            .append(c).append(w);
                }
            }
            sb.append(" ").append(row).append("\n");
        }

        // Add the last row based on the teamColor
        if (teamColor == ChessGame.TeamColor.WHITE) {
            sb.append(w).append(c).append("    h  g  f  e  d  c  b  a\n");
        } else {
            sb.append(w).append(c).append("    a  b  c  d  e  f  g  h\n");
        }

        return sb.toString();
    }

    private static char getPieceSymbol(ChessPiece piece) {
        return switch (piece.getPieceType()) {
            case ROOK -> 'R';
            case KNIGHT -> 'N';
            case BISHOP -> 'B';
            case QUEEN -> 'Q';
            case KING -> 'K';
            case PAWN -> 'P';
            default -> ' ';
        };
    }
}

// fails when 2 players join same game as same color
// board should rotate 180
// list games should show username of white and black player
// make sure quit shuts down program