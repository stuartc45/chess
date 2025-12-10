package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import datamodel.GameData;
import io.javalin.websocket.*;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketHandler {
    private final Connections connections = new Connections();
    private final DataAccess db;
    private final Set<Integer> finished = ConcurrentHashMap.newKeySet();

    public WebSocketHandler(DataAccess db) {
        this.db = db;
    }

    public void clear() {
        finished.clear();
    }

    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    public void handleMessage(WsMessageContext ctx) {
        try {
            UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            switch (command.getCommandType()) {
                case CONNECT -> join(command.getAuthToken(), command.getGameID(), ctx);
                case LEAVE -> leave(command.getAuthToken(), command.getGameID(), ctx);
                case RESIGN -> resign(command.getAuthToken(), command.getGameID(), ctx);
                case MAKE_MOVE -> {
                    MakeMoveCommand moveCommand = new Gson().fromJson(ctx.message(), MakeMoveCommand.class);
                    makeMove(moveCommand.getAuthToken(), moveCommand.getGameID(), moveCommand.getChessMove(), ctx);
                }
            }
        } catch (Exception ex) {
            connections.sendError(ctx, new ErrorMessage("Error"));
        }
    }

    private void join(String authToken, Integer gameID, WsMessageContext ctx) {
        try {
            connections.connect(gameID, ctx);
            var gameData = db.getGame(gameID);
            var game = gameData.game();
            String userName = db.getAuth(authToken).username();
            ServerMessage message = new LoadGameMessage(game);
            ctx.send(new Gson().toJson(message));
            if (gameData.whiteUsername().equals(userName)) {
                connections.sendNotification(ctx, gameID, new NotificationMessage(userName + " has joined the game as white"));
            } else if (gameData.blackUsername().equals(userName)) {
                connections.sendNotification(ctx, gameID, new NotificationMessage(userName + " has joined the game as black"));
            } else {
                connections.sendNotification(ctx, gameID, new NotificationMessage(userName + " has joined the game"));
            }

        } catch (DataAccessException | IOException ex) {
            connections.sendError(ctx, new ErrorMessage("Error"));
        }
    }

    private void leave(String authToken, Integer gameID, WsMessageContext ctx) {
        try {
            connections.close(ctx);
            String userName = db.getAuth(authToken).username();
            var game = db.getGame(gameID);
            if (userName.equalsIgnoreCase(game.whiteUsername())) {
                db.updateGame(gameID, null, game.blackUsername(), game.gameName());
            } else if (userName.equalsIgnoreCase(game.blackUsername())) {
                db.updateGame(gameID, game.whiteUsername(), null, game.gameName());
            } else {
                connections.sendError(ctx, new ErrorMessage("Error leaving the game"));
            }
            connections.sendNotification(ctx, gameID, new NotificationMessage(userName + " has left the game"));
        } catch (DataAccessException | IOException ex) {
            connections.sendError(ctx, new ErrorMessage("Error"));
        }
    }

    private void makeMove(String authToken, Integer gameID, ChessMove chessMove, WsMessageContext ctx) {
        try {
            GameData gameData = db.getGame(gameID);
            ChessGame game = gameData.game();
            String userName = db.getAuth(authToken).username();
            if (!gameData.whiteUsername().equals(userName) && !gameData.blackUsername().equals(userName)) {
                throw new IOException("Not allowed to make a move");
            }
            if (game.getTeamTurn() == ChessGame.TeamColor.WHITE) {
                if (!gameData.whiteUsername().equals(userName)) {
                    throw new IOException("Its not your turn");
                }
            } else if (game.getTeamTurn() == ChessGame.TeamColor.BLACK) {
                if (!gameData.blackUsername().equals(userName)) {
                    throw new IOException("Its not your turn");
                }
            }
            if (finished.contains(gameID)) {
                throw new IOException("Game is over");
            }
            game.makeMove(chessMove);
            db.updateGameState(gameID, game);
            connections.sendGame(ctx, gameID, new LoadGameMessage(game));
            String start = toChessNotation(
                    chessMove.getStartPosition().getRow(),
                    chessMove.getStartPosition().getColumn()
            );
            String end = toChessNotation(
                    chessMove.getEndPosition().getRow(),
                    chessMove.getEndPosition().getColumn()
            );
            String msg = userName + " moved from " + start + " to " + end;
            connections.sendNotification(ctx, gameID, new NotificationMessage(msg));

            if (game.isInCheckmate(ChessGame.TeamColor.WHITE)) {
                connections.sendNotificationAll(gameID, new NotificationMessage(gameData.whiteUsername() + " is in checkmate"));
            } else if (game.isInCheckmate(ChessGame.TeamColor.BLACK)) {
                connections.sendNotificationAll(gameID, new NotificationMessage(gameData.blackUsername() + " is in checkmate"));
            } else if (game.isInCheck(ChessGame.TeamColor.WHITE)) {
                connections.sendNotificationAll(gameID, new NotificationMessage(gameData.whiteUsername() + " is in check"));
            } else if (game.isInCheck(ChessGame.TeamColor.BLACK)) {
                connections.sendNotificationAll(gameID, new NotificationMessage(gameData.blackUsername() + " is in check"));
            }
        } catch (DataAccessException | IOException ex) {
            connections.sendError(ctx, new ErrorMessage("Error"));
        } catch (InvalidMoveException ex) {
            connections.sendError(ctx, new ErrorMessage("Error: Move not valid"));
        }
    }

    private void resign(String authToken, Integer gameID, WsMessageContext ctx) {
        try {
            GameData gameData = db.getGame(gameID);
            ChessGame game = gameData.game();
            String userName = db.getAuth(authToken).username();
            if (!gameData.whiteUsername().equals(userName) && !gameData.blackUsername().equals(userName)) {
                throw new Exception("Not allowed to resign");
            }
            if (game.getTeamTurn() == null) {
                throw new Exception("Game is over");
            }
            if (finished.contains(gameID)) {
                throw new Exception("Game is over");
            }
            game.setTeamTurn(null);
            db.updateGameState(gameID, game);
            finished.add(gameID);
            String msg = userName + " has resigned from the game";
            connections.sendNotificationAll(gameID, new NotificationMessage(msg));
        } catch (Exception ex) {
            connections.sendError(ctx, new ErrorMessage("Error"));
        }
    }

    private String toChessNotation(int row, int col) {
        char file = (char) ('a' + col - 1);
        return "" + file + row;
    }
}
