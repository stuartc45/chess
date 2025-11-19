package ui;

import exception.ResponseException;
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
            try {
                result = eval(line);
                System.out.println(result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }

        }
    }

    private String eval(String input) {
        try {
            String[] cmds = input.toLowerCase().split(" ");
            String cmd = cmds[0];
            String[] params = Arrays.copyOfRange(cmds, 1, cmds.length);
            return switch (cmd) {
                case "help" -> help();
                case "quit" -> "quit";
                case "login" -> login(params);
                case "register" -> register(params);
                case "logout" -> logout();
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "join" -> joinGame(params);
                case "observe" -> observeGame(params);
                default -> "";
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    private void printPrompt() {
        if (state == States.SIGNEDOUT) {
            System.out.println("\n" + SET_TEXT_COLOR_BLACK + "[LOGGED OUT] >>> " + SET_TEXT_COLOR_GREEN);
        } else {
            System.out.println("\n" + SET_TEXT_COLOR_BLACK + "[LOGGED IN] >>> " + SET_TEXT_COLOR_GREEN);
        }
    }

    private String help() {
        if (state == States.SIGNEDOUT) {
            return """
                    register <USERNAME> <PASSWORD> <EMAIL> - create an account
                    login <USERNAME> <PASSWORD> - log in
                    quit - exit the program
                    help - print possible commands""";
        } else if (state == States.SIGNEDIN) {
            return """
                    create <NAME> - create a game
                    list - lists the games
                    join <ID> [WHITE|BLACK] - join a game
                    observe <ID> - observe a game
                    logout
                    quit - exit the program
                    help - print possible commands""";
        }
        return """
        register <USERNAME> <PASSWORD> <EMAIL> - to create an account
        login <USERNAME> <PASSWORD> - to log in
        quit - to exit the program
        help - to print possible commands""";
    }

    private String login(String[] params) throws ResponseException {
        serverFacade.login(params[0], params[1]);
        state = States.SIGNEDIN;
        return String.format("Logged in as %s", params[0]);
    }

    private String register(String[] params) {
        serverFacade.register(params[0], params[1], params[2]);
        state = States.SIGNEDIN;
        return String.format("Logged in as %s", params[0]);
    }

    private String logout() throws ResponseException {
        assertSignedIn();
        serverFacade.logout();
        state = States.SIGNEDOUT;
        return "Logged out";
    }

    private String createGame(String[] params) throws ResponseException {
        assertSignedIn();
        serverFacade.createGame(params[0]);
        return String.format("Created game %s", params[0]);
    }

    private String listGames() throws ResponseException {
        assertSignedIn();
        serverFacade.listGames();
        return "fake list";
    }

    private String joinGame(String[] params) throws ResponseException {
        assertSignedIn();
        serverFacade.joinGame(params[0], params[1]);
        return String.format("Joined game %s", params[0]);
    }

    private String observeGame(String[] params) throws ResponseException {
        assertSignedIn();
        serverFacade.observeGame(params[0]);
        return String.format("Observing game %s", params[0]);
    }

    private void assertSignedIn() throws ResponseException {
        if (state == States.SIGNEDOUT) {
            throw new ResponseException(ResponseException.Code.ClientError, "You must sign in");
        }
    }
}
