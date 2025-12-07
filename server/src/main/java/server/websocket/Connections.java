package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class Connections {
    public final ConcurrentHashMap<Session, Session> connections = new ConcurrentHashMap<>();

    public void connect(Session session) {
        connections.put(session, session);
    }

    public void close(Session session) {
        connections.remove(session);
    }

    public void sendMessage(Session session, ServerMessage message) throws IOException {
        String msg = message.toString();
        for (Session c : connections.values()) {
            if (c.isOpen()) {
                if (!c.equals(session)) {
                    c.getRemote().sendString(msg);
                }
            }
        }
    }
}
