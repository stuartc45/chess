package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import datamodel.AuthData;
import datamodel.GameData;
import datamodel.JoinGameData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {
    @Test
    void createGameTest() throws Exception {
        DataAccess db = new MemoryDataAccess();
        var gameService = new GameService(db);
        var authData = new AuthData("joe", "xyz");
        db.addAuth(authData);
        var gameData = new GameData(null, null, null, "newGame", null);
        var gameID = gameService.createGame(authData.authToken(), gameData);
        assertEquals(1, gameID);
        assertNotNull(db.getGame(gameID));
    }

    @Test
    void createGameWithoutAuthToken() throws Exception {
        DataAccess db = new MemoryDataAccess();
        var gameService = new GameService(db);
        var gameData = new GameData(null, null, null, "newGame", null);
        assertThrows(Exception.class, () -> gameService.createGame("hello", gameData));
    }

    @Test
    void joinGameTest() throws Exception {
        DataAccess db = new MemoryDataAccess();
        var gameService = new GameService(db);
        var authData = new AuthData("joe", "xyz");
        db.addAuth(authData);
        var gameData = new GameData(null, null, null, "newGame", null);
        var gameID = gameService.createGame(authData.authToken(), gameData);
        gameService.joinGame(authData.authToken(), new JoinGameData("white", gameID));
        var game = db.getGame(gameID);
        assertNotNull(game.whiteUsername());
    }

    @Test
    void joinGameWhereColorNotNormal() throws Exception {
        DataAccess db = new MemoryDataAccess();
        var gameService = new GameService(db);
        var authData = new AuthData("joe", "xyz");
        db.addAuth(authData);
        var gameData = new GameData(null, "bill", null, "newGame", null);
        var gameID = gameService.createGame(authData.authToken(), gameData);
        assertThrows(Exception.class, () -> gameService.joinGame(authData.authToken(), new JoinGameData("green", gameID)));
    }

    @Test
    void listGamesTest() throws Exception {
        DataAccess db = new MemoryDataAccess();
        var gameService = new GameService(db);
        var authData = new AuthData("joe", "xyz");
        db.addAuth(authData);
        var gameData = new GameData(null, "bill", null, "newGame", null);
        var gameID = gameService.createGame(authData.authToken(), gameData);
        gameService.joinGame(authData.authToken(), new JoinGameData("white", gameID));
        var otherGame = new GameData(null, null, null, "otherGame", null);
        var otherGameID = gameService.createGame(authData.authToken(), otherGame);
        assertNotNull(gameService.listGames(authData.authToken()));
    }

    @Test
    void listGamesUnauthorized() throws Exception {
        DataAccess db = new MemoryDataAccess();
        var gameService = new GameService(db);
        var userService = new UserService(db);
        var authData = new AuthData("joe", "xyz");
        db.addAuth(authData);
        var gameData = new GameData(null, "bill", null, "newGame", null);
        var gameID = gameService.createGame(authData.authToken(), gameData);
        gameService.joinGame(authData.authToken(), new JoinGameData("white", gameID));
        var otherGame = new GameData(null, null, null, "otherGame", null);
        var otherGameID = gameService.createGame(authData.authToken(), otherGame);
        userService.logout(authData.authToken());
        assertThrows(Exception.class, () -> gameService.listGames(authData.authToken()));
    }
}