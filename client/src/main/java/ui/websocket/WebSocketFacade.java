package ui.websocket;

import java.net.URI;
import java.net.URISyntaxException;

import com.google.gson.Gson;
import jakarta.websocket.*;

public class WebSocketFacade {
    private Session session;
    private final Gson gson = new Gson();
            
    public WebSocketFacade(String url) {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
        } catch (URISyntaxException ex) {

        }
    }
}
