package server;

import java.net.http.HttpClient;

public class ServerFacade {
    private final String serverUrl;
    private final HttpClient client = HttpClient.newHttpClient();

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public void login(String username, String password) {

    }

    public void register(String username, String password, String email) {

    }

    public void logout() {

    }

    public void createGame(String gameName) {

    }

    public void listGames() {

    }

    public void joinGame(String gameID, String color) {

    }

    public void observeGame(String gameID) {

    }
}
