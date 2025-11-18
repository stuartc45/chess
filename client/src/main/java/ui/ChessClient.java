package ui;

import server.ServerFacade;

import java.util.Scanner;
import java.util.Arrays;
import static ui.EscapeSequences.*;

public class ChessClient {
    private final ServerFacade serverFacade;
    private States state = States.SIGNEDOUT;

    public ChessClient(String serverUrl) {
        this.serverFacade = new ServerFacade(serverUrl);
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to chess! Type \"help\" to get help!");

        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();
            result = eval(line);
            System.out.println(result);
        }
    }

    private String eval(String input) {
        String[] cmds = input.toLowerCase().split(" ");
        String cmd = cmds[0];
        String[] params = Arrays.copyOfRange(cmds, 1, cmds.length);
        return switch (cmd) {
            case "help" -> help();
            case "quit" -> "quit";
            case "login" -> login(params);
            case "register" -> register(params);
            default -> "";
        };
    }

    private void printPrompt() {
        System.out.print("\n" + SET_TEXT_COLOR_BLACK + ">>> " + SET_TEXT_COLOR_GREEN);
    }

    private String help() {
        return """
        register <USERNAME> <PASSWORD> <EMAIL> - to create an account
        login <USERNAME> <PASSWORD> - to log in
        quit - to exit the program
        help - to print possible commands""";
    }

    private String login(String[] params) {
        serverFacade.login(params);
        // TODO Add the functionality to switch to the logged in client or not
        return "";
    }

    private String register(String[] params) {
        serverFacade.register(params);
        // TODO Add functionality for switching to logged in client or not
        LoggedInClient loggedIn = new LoggedInClient(serverFacade);
        return "";
    }
}
