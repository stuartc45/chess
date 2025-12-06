package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.util.concurrent.ConcurrentHashMap;

public class Connections {
    public final ConcurrentHashMap<Session, Session> connections = new ConcurrentHashMap<>();

    public void connect(Session session) {
        connections.put(session, session);
    }

    public void close(Session session) {
        connections.remove(session);
    }
}
