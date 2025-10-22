package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import datamodel.GameData;
import datamodel.JoinGameData;
import datamodel.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {
    @Test
    void createGameTest() throws Exception {
        DataAccess db = new MemoryDataAccess();
        var gameService = new GameService(db);
        var userService = new UserService(db);
        var user = new UserData("joe", "j@j.com", "toomanysecrets");
        db.createUser(user);
        var authData = userService.login(user);
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
        var userService = new UserService(db);
        var user = new UserData("joe", "j@j.com", "toomanysecrets");
        db.createUser(user);
        var authData = userService.login(user);
        var gameData = new GameData(null, null, null, "newGame", null);
        var gameID = gameService.createGame(authData.authToken(), gameData);
        gameService.joinGame(authData.authToken(), new JoinGameData("white", gameID));
        var game = db.getGame(gameID);
        assertNotNull(game.whiteUsername());
    }
}