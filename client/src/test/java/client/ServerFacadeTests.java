package client;

import exception.ResponseException;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import java.net.URI;
import java.net.http.HttpRequest;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;
    private static String serverUrl;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverUrl = "http://localhost:" + port;
        facade = new ServerFacade(serverUrl);
    }

    @BeforeEach
    public void clearDb() {
        try {
            facade.clearDb();
        } catch (Throwable ex) {
            System.out.println(ex.getMessage());
        }
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    void register() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void badRegister() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        assertThrows(Exception.class, () -> facade.register("player1", "otherpassword", "email@email.com"));
    }

    @Test
    void login() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        var authData = facade.login("player1", "password");
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void badLogin() throws Exception {
        assertThrows(Exception.class, () -> facade.login("player1", "password"));
    }

    @Test
    void logout() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        facade.logout(authData.authToken());
        assertThrows(Exception.class, () -> facade.logout(authData.authToken()));
    }

    @Test
    void badLogout() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        assertThrows(Exception.class, () -> facade.logout(null));
    }

    @Test
    void createGame() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        var gameData = facade.createGame("game1", authData.authToken());
        assertTrue(gameData.gameID() != null);
    }

    @Test
    void badCreateGame() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        assertThrows(Exception.class, () -> facade.createGame("game1", "fake"));
    }

    @Test
    void listGames() throws Exception {
        var authToken = facade.register("joe", "joe", "joe").authToken();
        var gameData1 = facade.createGame("game1", authToken);
        var gameData2 = facade.createGame("game2", authToken);
        var games = facade.listGames(authToken);
        assertEquals(gameData1.gameID(), games.get(0).gameID());
        assertEquals(gameData2.gameID(), games.get(1).gameID());
    }

    @Test
    void badListGames() throws Exception {
        var authToken = facade.register("joe", "joe", "joe").authToken();
        var gameData1 = facade.createGame("game1", authToken);
        var gameData2 = facade.createGame("game2", authToken);
        assertThrows(Exception.class, () -> facade.listGames(null));
    }

    @Test
    void joinGame() throws Exception {
        var authToken = facade.register("joe", "joe", "joe").authToken();
        var gameData = facade.createGame("game1", authToken);
        facade.joinGame(gameData.gameID(), "white", authToken);
        assertThrows(Exception.class, () -> facade.joinGame(gameData.gameID(), "white", authToken));
    }

    @Test
    void badJoinGame() throws Exception {
        var authToken = facade.register("joe", "joe", "joe").authToken();
        var gameData = facade.createGame("game1", authToken);
        assertThrows(Exception.class, () -> facade.joinGame(null, "white", authToken));

    }
}
