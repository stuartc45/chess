package server.websocket;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.SqlDataAccess;
import io.javalin.websocket.*;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final Connections connections = new Connections();

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        try {
            UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            switch (command.getCommandType()) {
                case CONNECT -> join(command.getAuthToken(), command.getGameID(), ctx.session);
                case LEAVE -> leave(command.getAuthToken(), command.getGameID(), ctx.session);
                case RESIGN -> resign(command.getAuthToken(), command.getGameID(), ctx.session);
            }
        } catch (Exception ex) {

        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void join(String authToken, Integer gameID, Session session) {
        try {
            connections.connect(session);
            String userName = new SqlDataAccess().getAuth(authToken).username();

        } catch (DataAccessException ex) {

        }

    }

    private void leave(String authToken, Integer gameID, Session session) {
        try {
            connections.close(session);
            String userName = new SqlDataAccess().getAuth(authToken).username();

        } catch (DataAccessException ex) {

        }
    }

    private void resign(String authToken, Integer gameID, Session session) {

    }
}
