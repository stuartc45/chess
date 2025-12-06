package server.websocket;

import io.javalin.websocket.WsConnectContext;

import java.util.HashSet;
import java.util.Set;

public class WebSocketHandler {
    private static final Set<WsConnectContext> connections = new HashSet<>();

    public void connect(WsConnectContext ctx) {
        connections.add(ctx);
        System.out.println("Websocket connected");
    }

    public void close(WsConnectContext ctx) {
        connections.remove(ctx);
        System.out.println("Websocket closed");
    }
}
