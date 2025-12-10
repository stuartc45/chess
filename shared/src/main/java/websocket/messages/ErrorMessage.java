package websocket.messages;

import com.google.gson.Gson;

public class ErrorMessage extends ServerMessage {
    private final String errorMessage;

    public ErrorMessage(String errorMessage) {
        super(ServerMessageType.ERROR);
        this.errorMessage = errorMessage;
    }

    public String getJson() {
        return new Gson().toJson(this);
    }

    public String getMessage() {
        return errorMessage;
    }
}
