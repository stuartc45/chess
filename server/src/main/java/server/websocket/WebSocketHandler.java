package server.websocket;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.SqlDataAccess;
import io.javalin.websocket.*;
import org.eclipse.jetty.websocket.api.Session;
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

    private void resign(String authToken, Integer gameID, WsMessageContext ctx) {

    }
}
