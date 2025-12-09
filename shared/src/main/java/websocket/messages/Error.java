package websocket.messages;

import com.google.gson.Gson;

public class Error extends ServerMessage {
    private final String message;

    public Error(String message) {
        super(ServerMessageType.ERROR);
        this.message = message;
    }

    public String getJson() {
        return new Gson().toJson(this);
    }

    public String getMessage() {
        return message;
    }
}
