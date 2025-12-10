package ui;

import chess.*;
import com.google.gson.Gson;
import datamodel.*;
import exception.ErrorResponse;
import serverfacade.ServerFacade;
import ui.websocket.NotificationHandler;
import ui.websocket.WebSocketFacade;
import websocket.messages.ServerMessage;

import java.util.HashMap;
import java.util.Scanner;
import java.util.Arrays;
import static ui.EscapeSequences.*;

public class ChessClient implements NotificationHandler {
    private final ServerFacade serverFacade;
    private States state = States.SIGNEDOUT;
    private String authToken;
    private final WebSocketFacade ws;
//    private HashMap<Integer, Integer> gameMap;
//    private Integer clientGameId = 1;
    private ChessGame currentGame = null;
    private Integer currentGameID = null;
    private ChessGame.TeamColor currentColor;

    public ChessClient(String serverUrl) throws Exception {
        this.serverFacade = new ServerFacade(serverUrl);
//        gameMap = new HashMap<>();
        ws = new WebSocketFacade(serverUrl, this);
    }

    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case NOTIFICATION -> {
                var msg = (websocket.messages.Notification) message;
                System.out.println(msg.getMessage());
                printPrompt();
            }
            case ERROR -> {
                var msg = (websocket.messages.Error) message;
                System.out.println(msg.getMessage());
                printPrompt();
            }
            case LOAD_GAME -> {
                var msg = (websocket.messages.LoadGame) message;
                currentGame = msg.getGame();
                System.out.println(RESET_TEXT_COLOR);
                PrintChessBoard printer = new PrintChessBoard(currentColor);
                printer.printBoard(currentGame.getBoard());
                System.out.println(SET_TEXT_COLOR_GREEN);
                printPrompt();
            }
        }
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
                case "showmoves" -> highlightMoves(params);
                case "redraw" -> redrawBoard();
                case "move" -> makeChessMove(params);
                case "leave" -> leaveGame();
                case "resign" -> resign();
                default -> "";
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private void printPrompt() {
        if (state == States.SIGNEDOUT) {
            System.out.println("\n" + SET_TEXT_COLOR_BLUE + "[LOGGED OUT] >>> " + SET_TEXT_COLOR_GREEN);
        } else if (state == States.SIGNEDIN) {
            System.out.println("\n" + SET_TEXT_COLOR_YELLOW + "[LOGGED IN] >>> " + SET_TEXT_COLOR_GREEN);
        } else {
            System.out.println("\n" + SET_TEXT_COLOR_RED + "[IN GAME] >>> " + SET_TEXT_COLOR_GREEN);
        }
    }

    private String help() {
        if (state == States.GAMEPLAY) {
            return """
                    redraw - redraws the chess board
                    leave - leaves the game
                    resign - forfeit and end the game
                    move <POSITION> <POSITION> - make a chess move
                    showmoves <POSITION> - shows the legal moves for a piece
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

    private String login(String[] params) throws Exception {
        assertSignedOut();
        if (params.length < 2) {
            return "Please enter both a username and password";
        } else if (params.length > 2) {
            return "Please only enter a username and password";
        }
        try {
            authToken = serverFacade.login(params[0], params[1]).authToken();
            state = States.SIGNEDIN;
            return String.format("Logged in as %s", params[0]);
        } catch (Exception ex) {
            String errMessage = getErrorMessage(ex);
            throw new Exception("Login failed with " + errMessage);
        }
    }

    private String register(String[] params) throws Exception {
        assertSignedOut();
        if (params.length < 3) {
            return "Please enter username, password, and email";
        } else if (params.length > 3) {
            return "Please only enter username, password, and email";
        }
        try {
            authToken = serverFacade.register(params[0], params[1], params[2]).authToken();
            state = States.SIGNEDIN;
            return String.format("Logged in as %s", params[0]);
        } catch (Exception ex) {
            String errMessage = getErrorMessage(ex);
            throw new Exception("Register failed with " + errMessage);
        }
    }

    private String logout() throws Exception {
        assertSignedIn();
        try {
            serverFacade.logout(authToken);
            state = States.SIGNEDOUT;
            return "Logged out";
        } catch (Exception ex) {
            String errMessage = getErrorMessage(ex);
            throw new Exception("Logout failed with " + errMessage);
        }
    }

    private String createGame(String[] params) throws Exception {
        assertSignedIn();
        if (params.length == 0) {
            return "Please include a gameName";
        }
        if (params.length > 1) {
            return "Please only include a gameName";
        }
        try {
            GameData gameData = serverFacade.createGame(params[0], authToken);

            return String.format("Created game %s", params[0]);
        } catch (Exception ex) {
            String errMessage = getErrorMessage(ex);
            throw new Exception("Create failed with " + errMessage);
        }
    }

    private String listGames() throws Exception {
        assertSignedIn();
        try {
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
        } catch (Exception ex) {
            String errMessage = getErrorMessage(ex);
            throw new Exception("List failed with " + errMessage);
        }
    }

    private String joinGame(String[] params) throws Exception {
        assertSignedIn();
        if (params.length < 2) {
            return "Please include both the ID of the game and your desired color";
        }
        if (params.length > 2) {
            return "Please only include the ID of the game and your desired color";
        }
        try {

            Integer gameID = Integer.valueOf(params[0]);
            serverFacade.joinGame(gameID, params[1], authToken);
            state = States.GAMEPLAY;
            currentGameID = gameID;
            if (params[1].equalsIgnoreCase("white")) {
                currentColor = ChessGame.TeamColor.WHITE;
            } else {
                currentColor = ChessGame.TeamColor.BLACK;
            }
            ws.joinGame(authToken, gameID);
            System.out.println(RESET_TEXT_COLOR);
            System.out.println(SET_TEXT_COLOR_GREEN);
            return String.format("Joined game %s", params[0]);
        } catch (NumberFormatException e) {
            throw new Exception("Please enter a numerical value");
        } catch (Exception ex) {
            String errMessage = getErrorMessage(ex);
            throw new Exception("Join failed with " + errMessage);
        }
    }

    private String observeGame(String[] params) throws Exception {
        assertSignedIn();
        if (params.length == 0) {
            return "Please include the ID of the game you wish to observe";
        }
        if (params.length > 1) {
            return "Please only include the ID of the game you wish to observe";
        }
        try {
            Integer listID = Integer.valueOf(params[0]);
//            if (!gameMap.containsKey(listID)) {
//                throw new Exception("Enter a valid game ID");
//            }
            // you're going to need something that checks for this ^^^
            ChessBoard board = new ChessBoard();
            board.resetBoard();
//            PrintChessBoard printChessBoard = new PrintChessBoard("white");
//            printChessBoard.printBoard(board);
            return String.format("Observing game %s", params[0]);
        } catch (NumberFormatException e) {
            throw new Exception("Please enter a numerical value");
        } catch (Exception ex) {
            throw new Exception("Observe failed with " + ex.getMessage());
        }
    }

    private String leaveGame() throws Exception {
        assertInGame();
        ws.leaveGame(authToken, currentGameID);
        state = States.SIGNEDIN;
        return "You have left the game";
    }

    private String highlightMoves(String[] params) throws Exception {
        assertInGame();
        if (params.length > 1) {
            throw new Exception("Only include one position to highlight");
        }
        ChessPosition position = parseSquare(params[0]);
        PrintChessBoard printer = new PrintChessBoard(currentColor);
        printer.printHighlightBoard(currentGame, position);
        return "";
    }

    private String redrawBoard() throws Exception {
        assertInGame();
        PrintChessBoard printer = new PrintChessBoard(currentColor);
        printer.printBoard(currentGame.getBoard());
        return "";
    }

    private String makeChessMove(String[] params) throws Exception {
        if (params.length > 2) {
            throw new Exception("Only include the start and end positions of the piece you want to move");
        }
        if (params.length < 2) {
            throw new Exception("Please include both the start and end positions of the piece you want to move");
        }
        try {
            ChessPosition moveFrom = parseSquare(params[0]);
            ChessPosition moveTo = parseSquare(params[1]);
            boolean promotion;
            promotion = checkPromotion(moveFrom, moveTo);
            ChessPiece.PieceType promotionPiece = null;
            if (promotion) {
                promotionPiece = getPromotionPiece();
            }
            ChessMove move = new ChessMove(moveFrom, moveTo, promotionPiece);
            ws.makeMove(authToken, currentGameID, move);
            return "";
        } catch (Exception ex) {
            throw new Exception("Make move not valid");
        }
    }

    private ChessPosition parseSquare(String square) throws Exception {
        if (square == null || square.length() != 2) {
            throw new Exception("Incorrect position. Expected positions examples: a2, h7, etc.");
        }

        char c = Character.toLowerCase(square.charAt(0));
        char r = square.charAt(1);
        if (c < 'a' || c > 'h') {
            throw new Exception("Column must be a–h");
        }
        if (r < '1' || r > '8') {
            throw new Exception("Row must be 1–8");
        }

        int col = c - 'a' + 1;
        int row = r - '0';

        return new ChessPosition(row, col);
    }

    private ChessPiece.PieceType getPromotionPiece() {
        while (true) {
            System.out.println("What piece would you like to promote to?  ");
            String line = new Scanner(System.in).nextLine().toLowerCase();
            String[] cmds = line.split(" ");
            String cmd = cmds[0];
            switch (cmd) {
                case "queen" -> {
                    return ChessPiece.PieceType.QUEEN;
                }
                case "rook" -> {
                    return ChessPiece.PieceType.ROOK;
                }
                case "bishop" -> {
                    return ChessPiece.PieceType.BISHOP;
                }
                case "knight" -> {
                    return ChessPiece.PieceType.KNIGHT;
                }
                default -> {
                    System.out.println("Please enter a valid promotion piece type");
                }
            }
        }
    }

    private boolean checkPromotion(ChessPosition start, ChessPosition end) {
        if (currentGame.getBoard().getPiece(start).getPieceType() != ChessPiece.PieceType.PAWN) {
            return false;
        }
        if (currentColor == ChessGame.TeamColor.WHITE && end.getRow() != 8) {
            return false;
        }
        if (currentColor == ChessGame.TeamColor.BLACK && end.getRow() != 1) {
            return false;
        }
        return true;
    }

    private String resign() throws Exception {
        assertInGame();
        System.out.println("Are you sure you want to resign? (y/n) ");
        String line = new Scanner(System.in).nextLine().toLowerCase();
        if (!line.equals("y")) {
            return "Resign cancelled";
        }
        ws.resignGame(authToken, currentGameID);
        return "";
    }

    private String clearDb() throws Exception {
        assertSignedIn();
        try {
            serverFacade.clearDb();
            state = States.SIGNEDOUT;
//            gameMap.clear();
//            clientGameId = 1;
            return "Database cleared";
        } catch (Exception ex) {
            String errMessage = getErrorMessage(ex);
            throw new Exception("Clear failed with " + errMessage);
        }
    }

    private void assertInGame() throws Exception {
        if (state == States.SIGNEDIN) {
            throw new Exception("You must join a game first");
        } else if (state == States.SIGNEDOUT) {
            throw new Exception("You must sign in");
        }
    }

    private void assertSignedIn() throws Exception {
        if (state == States.SIGNEDOUT) {
            throw new Exception("You must sign in");
        } else if (state == States.GAMEPLAY) {
            throw new Exception("You must leave the game first");
        }
    }

    private void assertSignedOut() throws Exception {
        if (state == States.SIGNEDIN) {
            throw new Exception("You're already signed in");
        } else if (state == States.GAMEPLAY) {
            throw new Exception("You're already signed in");
        }
    }

    private String getErrorMessage(Exception ex) {
        ErrorResponse err = new Gson().fromJson(ex.getMessage(), ErrorResponse.class);
        return err.message;
    }
}
