package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import exception.ResponseException;
import ui.websocket.ServerMessageHandler;
import webSocketMessages.serverMessages.ServerMessage;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl implements ServerMessageHandler {

    private static final String UNICODE_ESCAPE = "\u001b";
    private static final String SET_BG_COLOR = UNICODE_ESCAPE + "[48;5;";
    private static final String SET_TEXT_COLOR = UNICODE_ESCAPE + "[38;5;";
    public static final String SET_BG_COLOR_LIGHT_GREY = SET_BG_COLOR + "242m";
    public static final String SET_BG_COLOR_DARK_GREY = SET_BG_COLOR + "235m";
    public static final String SET_TEXT_COLOR_RED = SET_TEXT_COLOR + "160m";
    public static final String SET_TEXT_COLOR_BLUE = SET_TEXT_COLOR + "12m";
    public static final String SET_TEXT_COLOR_WHITE = SET_TEXT_COLOR + "15m";
    public static final String RESET_BG_COLOR = SET_BG_COLOR + "0m";
    public static final String SET_BG_COLOR_GREEN = SET_BG_COLOR + "46m";
    private final ChessClient client;
    private State state = State.LOGGEDOUT;

    public ChessGame game;


    public Repl(String serverUrl) throws ResponseException {
        client = new ChessClient(serverUrl, this);
    }

    public void run() {
        System.out.println(RESET_BG_COLOR + WHITE_QUEEN + "Welcome to 240 Chess. Type Help to get started." + WHITE_QUEEN);
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    public void notify(ServerMessage serverMessage) {
        System.out.println("Client received message in repl: " + serverMessage.toString());
        if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
            ChessGame.TeamColor playerColor = client.getPlayerColor();
            if (playerColor == null) {
                playerColor = ChessGame.TeamColor.WHITE;
            }
            ChessGame game = serverMessage.getGame();
            System.out.println(drawChessBoard(game, playerColor));
            this.game = game;
        } else {
            System.out.println("notify called" + serverMessage.toString());
            System.out.println(serverMessage.getMessage());
        }
        printPrompt();
    }

    private void printPrompt() {
        System.out.print("\n" + SET_TEXT_COLOR_WHITE + " >>> " + SET_TEXT_COLOR_GREEN + SET_TEXT_BLINKING);
    }

    public static String drawChessBoard(ChessGame game, ChessGame.TeamColor teamColor) {
        int a = 1;
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
        a = 1;
        // Add the first row based on the teamColor
        if (teamColor == ChessGame.TeamColor.BLACK) {
            sb.append(w).append(c).append("\n    h  g  f  e  d  c  b  a\n");
        } else {
            sb.append(w).append(c).append("\n    a  b  c  d  e  f  g  h\n");
        }

        int startRow = currentTeamColor == ChessGame.TeamColor.WHITE ? 8 : 1;
        int endRow = currentTeamColor == ChessGame.TeamColor.WHITE ? 0 : 9;
        int rowStep = currentTeamColor == ChessGame.TeamColor.WHITE ? -1 : 1;

        int startCol = currentTeamColor == ChessGame.TeamColor.BLACK ? 8 : 1;
        int endCol = currentTeamColor == ChessGame.TeamColor.BLACK ? 0 : 9;
        int colStep = currentTeamColor == ChessGame.TeamColor.BLACK ? -1 : 1;

        a = 1;
        for (int row = startRow; row != endRow; row += rowStep) {
            sb.append(" ").append(row).append(" ");
            for (int col = startCol; col != endCol; col += colStep) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if (piece == null) {
                    a = 1;
                    sb.append(r).append((row + col) % 2 == 0 ? l : d).append("   ").append(c).append(w);
                } else {
                    char pieceChar = getPieceSymbol(piece);
                    sb.append(piece.getTeamColor() == ChessGame.TeamColor.WHITE ? b : r)
                            .append((row + col) % 2 == 0 ? l : d)
                            .append(" ").append(pieceChar).append(" ")
                            .append(c).append(w);
                }
            }
            sb.append(" ").append(row).append("\n");
        }
        a = 1;
        // Add the last row based on the teamColor
        if (teamColor == ChessGame.TeamColor.BLACK) {
            sb.append(w).append(c).append("    h  g  f  e  d  c  b  a\n");
        } else {
            sb.append(w).append(c).append("    a  b  c  d  e  f  g  h\n");
        }
        a = 1;
        return sb.toString();
    }

    private static char getPieceSymbol(ChessPiece piece) {
        int a = 1;
        return switch (piece.getPieceType()) {
            case BISHOP -> 'B';
            case KNIGHT -> 'N';
            case QUEEN -> 'Q';
            case ROOK -> 'R';
            case KING -> 'K';
            case PAWN -> 'P';
            default -> ' ';
        };
    }

}