package ui;

import com.google.gson.Gson;
import datamodel.*;
import exception.ResponseException;
import server.ServerFacade;

import java.util.HashMap;
import java.util.Scanner;
import java.util.Arrays;
import static ui.EscapeSequences.*;

public class ChessClient {
    private final ServerFacade serverFacade;
    private States state = States.SIGNEDOUT;
    private String authToken;
    private HashMap<Integer, Integer> gameMap;
    private Integer clientGameId = 1;

    public ChessClient(String serverUrl) {
        this.serverFacade = new ServerFacade(serverUrl);
        gameMap = new HashMap<>();
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
                case "clear" -> clearDb();
                default -> "";
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    private void printPrompt() {
        if (state == States.SIGNEDOUT) {
            System.out.println("\n" + SET_TEXT_COLOR_BLUE + "[LOGGED OUT] >>> " + SET_TEXT_COLOR_GREEN);
        } else {
            System.out.println("\n" + SET_TEXT_COLOR_YELLOW + "[LOGGED IN] >>> " + SET_TEXT_COLOR_GREEN);
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
        if (params.length < 2) {
            return "Please enter both a username and password";
        } else if (params.length > 2) {
            return "Please only enter a username and password";
        }
        authToken = serverFacade.login(params[0], params[1]).authToken();
        state = States.SIGNEDIN;
        return String.format("Logged in as %s", params[0]);
    }

    private String register(String[] params) throws ResponseException {
        if (params.length < 3) {
            return "Please enter username, password, and email";
        } else if (params.length > 3) {
            return "Please only enter username, password, and email";
        }
        authToken = serverFacade.register(params[0], params[1], params[2]).authToken();
        state = States.SIGNEDIN;
        return String.format("Logged in as %s", params[0]);
    }

    private String logout() throws ResponseException {
        assertSignedIn();
        serverFacade.logout(authToken);
        state = States.SIGNEDOUT;
        return "Logged out";
    }

    private String createGame(String[] params) throws ResponseException {
        if (params.length == 0) {
            return "Please include a gameName";
        }
        if (params.length > 1) {
            return "Please only include a gameName";
        }
        assertSignedIn();
        GameData gameData = serverFacade.createGame(params[0], authToken);
        gameMap.put(clientGameId, gameData.gameID());
        clientGameId++;
        return String.format("Created game %s", params[0]);
    }

    private String listGames() throws ResponseException {
        assertSignedIn();
        var games = serverFacade.listGames(authToken);
        var result = new StringBuilder();
        for (int i = 0; i < games.size(); i++) {
            var game = games.get(i);
            int gameSpot = i + 1;
            String white = (game.whiteUsername() == null) ? "---" : game.whiteUsername();
            String black = (game.blackUsername() == null) ? "---" : game.blackUsername();
            result.append("Game ")
                    .append(gameSpot).append(": ")
                    .append("GameName: ").append(game.gameName())
                    .append(" White: ").append(white)
                    .append(" Black: ").append(black)
                    .append("\n");
        }
        return result.toString();
    }

    private String joinGame(String[] params) throws ResponseException {
        if (params.length < 2) {
            return "Please include both the ID of the game and your desired color";
        }
        if (params.length > 2) {
            return "Please only include the ID of the game and your desired color";
        }
        assertSignedIn();
        Integer gameID = gameMap.get(Integer.valueOf(params[0]));
        serverFacade.joinGame(gameID, params[1], authToken);
        return String.format("Joined game %s", params[0]);
    }

    private String observeGame(String[] params) throws ResponseException {
        if (params.length == 0) {
            return "Please include the ID of the game you wish to observe";
        }
        if (params.length > 1) {
            return "Please only include the ID of the game you wish to observe";
        }
        assertSignedIn();
        Integer listID = Integer.valueOf(params[0]);
        return String.format("Observing game %s", params[0]);
    }

    private String clearDb() throws ResponseException {
        assertSignedIn();
        serverFacade.clearDb();
        state = States.SIGNEDOUT;
        gameMap.clear();
        clientGameId = 1;
        return "Database cleared";
    }

    private void assertSignedIn() throws ResponseException {
        if (state == States.SIGNEDOUT) {
            throw new ResponseException(ResponseException.Code.ClientError, "You must sign in");
        }
    }
}
