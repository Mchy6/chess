package ui;

import exception.ResponseException;
import ui.websocket.ServerMessageHandler;
import webSocketMessages.serverMessages.ServerMessage;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl implements ServerMessageHandler {
    private final ChessClient client;
    private State state = State.LOGGEDOUT;


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
//        System.out.println(SET_TEXT_COLOR_RED + serverMessage.message());
        System.out.println("Should print out notification/error/load game msg here");
        printPrompt();
    }

    private void printPrompt() {
        System.out.print("\n" + SET_TEXT_COLOR_WHITE + " >>> " + SET_TEXT_COLOR_GREEN + SET_TEXT_BLINKING);
    }

}