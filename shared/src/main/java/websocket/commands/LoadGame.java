package websocket.commands;

import websocket.messages.ServerMessage;

public class LoadGame extends ServerMessage {
    private final String game = "temporary string";

    public LoadGame() {
        super(ServerMessageType.LOAD_GAME);
    }
}
