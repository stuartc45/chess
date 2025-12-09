package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.SqlDataAccess;
import datamodel.GameData;
import io.javalin.websocket.*;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.Error;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class WebSocketHandler {
    private final Connections connections = new Connections();
    private final DataAccess db;

    public WebSocketHandler(DataAccess db) {
        this.db = db;
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
            System.out.println(ex.getMessage());
        }
    }

    private void join(String authToken, Integer gameID, WsMessageContext ctx) {
        try {
            connections.connect(gameID, ctx);
            var game = db.getGame(gameID).game();
            ServerMessage message = new LoadGame(game);
            ctx.send(new Gson().toJson(message));
            String userName = db.getAuth(authToken).username();
            connections.sendNotification(ctx, gameID, new Notification(userName + " has joined the game"));
        } catch (DataAccessException ex) {

        } catch (IOException ex) {

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
                connections.sendError(ctx, new Error("Error leaving the game"));
            }
            connections.sendNotification(ctx, gameID, new Notification(userName + " has left the game"));
        } catch (DataAccessException ex) {

        } catch (IOException ex) {

        }
    }

    private void makeMove(String authToken, Integer gameID, ChessMove chessMove, WsMessageContext ctx) {
        try {
            GameData gameData = db.getGame(gameID);
            ChessGame game = gameData.game();
            game.makeMove(chessMove);
            db.updateGameState(gameID, game);
            connections.sendGame(ctx, gameID, new LoadGame(game));
            String start = toChessNotation(
                    chessMove.getStartPosition().getRow(),
                    chessMove.getStartPosition().getColumn()
            );
            String end = toChessNotation(
                    chessMove.getEndPosition().getRow(),
                    chessMove.getEndPosition().getColumn()
            );
            String userName = db.getAuth(authToken).username();
            String msg = userName + " moved from " + start + " to " + end;
            connections.sendNotification(ctx, gameID, new Notification(msg));

            if (game.isInCheck(ChessGame.TeamColor.WHITE)) {
                connections.sendNotification(null, gameID, new Notification("White is in check"));
            }
            if (game.isInCheck(ChessGame.TeamColor.BLACK)) {
                connections.sendNotification(null, gameID, new Notification("Black is in check"));
            }
            if (game.isInCheckmate(ChessGame.TeamColor.WHITE)) {
                connections.sendNotification(null, gameID, new Notification("White is in checkmate"));
            }
            if (game.isInCheckmate(ChessGame.TeamColor.BLACK)) {
                connections.sendNotification(null, gameID, new Notification("Black is in checkmate"));
            }
        } catch (DataAccessException | IOException ex) {
            System.out.println("failed to make the move");
        } catch (InvalidMoveException ex) {
            connections.sendError(ctx, new Error("Error: Move not valid"));
        }
    }

    private void resign(String authToken, Integer gameID, WsMessageContext ctx) {

    }

    private String toChessNotation(int row, int col) {
        char file = (char) ('a' + col - 1);
        return "" + file + row;
    }
}
