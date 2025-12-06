package server.websocket;

import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsConnectContext;
import org.eclipse.jetty.websocket.api.Session;

import java.util.HashSet;
import java.util.Set;

public class WebSocketHandler {
    private final Connections connections = new Connections();

    public void connect(WsConnectContext ctx) {
        connections.add(ctx);
        System.out.println("Websocket connected");
    }

    public void close(WsCloseContext ctx) {
        connections.remove(ctx);
        System.out.println("Websocket closed");
    }
}
