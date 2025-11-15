package ui;

import server.ServerFacade;

import java.util.Scanner;
import static ui.EscapeSequences.*;

public class LoggedOutClient {
    private final ServerFacade server;

    public LoggedOutClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to 240 chess");
        printPrompt();

        var result = "";
        result = scanner.nextLine();
        if (result.equals("hello")) {
            System.out.println("Good to see you");
        } else {
            System.out.println("I don't know you");
        }
    }

    private void printPrompt() {
        System.out.print("\n" + SET_TEXT_COLOR_BLACK + ">>> " + SET_TEXT_COLOR_GREEN);
    }
}
