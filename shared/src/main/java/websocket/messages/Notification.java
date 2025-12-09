package websocket.messages;

import com.google.gson.Gson;

public class Notification extends ServerMessage {
    private final String message;
    public Notification(String message) {
        super(ServerMessageType.NOTIFICATION);
        this.message = message;
    }

    public String getJson() {
        return new Gson().toJson(this);
    }

    public String getMessage() {
        return message;
    }
}
