package webSocketMessages.serverMessages;

import chess.*;

import java.util.Collection;

public class smLoadGame extends ServerMessage {
    private ChessGame game;
    private boolean highlight;
    private static ChessGame.TeamColor teamColor;
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


    public smLoadGame(ChessGame game, ChessGame.TeamColor teamColor) {
        this.teamColor = teamColor;
        this.highlight = false;
        this.serverMessageType = ServerMessageType.LOAD_GAME;
        if(this.highlight) {
//            this.setMessage(highlight(game));
        } else {
            this.setMessage(drawChessBoard(game));
        }
        this.setMessage(drawChessBoard(game));
        this.game = game;
    }

    public ChessGame getGame() {
        return game;
    }
    public String getMessage() {
        if(highlight) {
//            return highlight(game);
            return "";
        } else {
            return drawChessBoard(game);
        }
    }



    public static String drawChessBoard(ChessGame game) {
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

        sb.append(w).append(c).append("\n    a  b  c  d  e  f  g  h\n");

        int startRow = currentTeamColor == ChessGame.TeamColor.WHITE ? 8 : 1;
        int endRow = currentTeamColor == ChessGame.TeamColor.WHITE ? 0 : 9;
        int rowStep = currentTeamColor == ChessGame.TeamColor.WHITE ? -1 : 1;

        for (int row = startRow; row != endRow; row += rowStep) {
            sb.append(" ").append(row).append(" ");
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if (piece == null) {
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

        sb.append(w).append(c).append("    a  b  c  d  e  f  g  h\n");
        return sb.toString();
    }

    public static String highlight(ChessGame game, ChessPosition position) {
        ChessBoard board = game.getBoard();
        Collection<ChessMove> moves = game.validMoves(position);



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