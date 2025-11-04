package dataaccess;

import chess.ChessGame;
import datamodel.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.GameService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SqlDataAccessTest {

    @BeforeEach
    public void clearDB() throws DataAccessException {
        DataAccess db = new SqlDataAccess();
        db.clear();
    }

    @Test
    void clear() throws DataAccessException, SQLException {
        DataAccess db = new SqlDataAccess();
        var user = new UserData("joe", "j@j.com", "toomanysecrets");
        var authData = new AuthData("joe", "xyz");
        db.createUser(user);
        db.addAuth(authData);
        db.clear();
        assertNull(db.getAuth(authData.authToken()));
        assertNull(db.getUser(user.username()));
    }

    @Test
    void createUser() throws SQLException, DataAccessException {
        DataAccess db = new SqlDataAccess();
        var user = new UserData("joe", "toomanysecrets", "j@j.com");
        db.createUser(user);
        assertEquals(user, db.getUser(user.username()));
    }

    @Test
    void createBadUser() throws SQLException, DataAccessException {
        DataAccess db = new SqlDataAccess();
        var user = new UserData(null, "toomanysecrets", "j@j.com");
        assertThrows(Exception.class, () -> db.createUser(user));
    }

    @Test
    void getUser() {
    }

    @Test
    void getUserBad() {

    }

    @Test
    void getAuth() {
    }

    @Test
    void getAuthBad() {

    }

    @Test
    void deleteAuth() throws DataAccessException {
        DataAccess db = new SqlDataAccess();
        var authData = new AuthData("joe", "xyz");
        db.addAuth(authData);
        db.deleteAuth(authData.authToken());
        assertNull(db.getAuth(authData.authToken()));
    }

    @Test
    void deleteAuthBad() {

    }

    @Test
    void addAuth() throws DataAccessException {
        DataAccess db = new SqlDataAccess();
        var authData = new AuthData("joe", "xyz");
        db.addAuth(authData);
        assertEquals(authData, db.getAuth(authData.authToken()));
    }

    @Test
    void addAuthBad() {
        
    }

    @Test
    void createGame() throws DataAccessException {
        DataAccess db = new SqlDataAccess();
        var gameData = new GameData(1, null, null, "game", new ChessGame());
        db.createGame(gameData);
        assertEquals(gameData, db.getGame(1));
    }

    @Test
    void createBadGame() throws DataAccessException {
        DataAccess db = new SqlDataAccess();
        var gameData = new GameData(null, null, null, "game1", new ChessGame());
        var game2 = new GameData(1, null, null, "game2", new ChessGame());
        db.createGame(gameData);
        assertThrows(Exception.class, () -> db.createGame(game2));
    }

    @Test
    void getGame() {
    }

    @Test
    void getGameBad() {

    }

    @Test
    void updateGame() throws DataAccessException {
        DataAccess db = new SqlDataAccess();
        var gameData = new GameData(1, null, null, "game", new ChessGame());
        db.createGame(gameData);
        db.updateGame(1, "player1", null, "game");
        var game = db.getGame(1);
        assertEquals("player1", game.whiteUsername());
        assertEquals(game.blackUsername(), gameData.blackUsername());
    }

    @Test
    void updateGameBad() {

    }

    @Test
    void getGameList() throws DataAccessException, SQLException {
        DataAccess db = new SqlDataAccess();
        var gameData = new GameData(1, null, null, "game", new ChessGame());
        db.createGame(gameData);
        var gameData1 = new GameData(2, null, null, "game2", new ChessGame());
        db.createGame(gameData1);
        var gameData2 = new GameData(3, null, null, "game3", new ChessGame());
        db.createGame(gameData2);
        var gameData3 = new GameData(4, null, null, "game4", new ChessGame());
        db.createGame(gameData3);
        List<GameData> listOfGames = new ArrayList<>();
        listOfGames.add(gameData);
        listOfGames.add(gameData1);
        listOfGames.add(gameData2);
        listOfGames.add(gameData3);
        var gameList = db.getGameList();
        assertEquals(listOfGames, gameList);
    }

    @Test
    void getGameListBad() {

    }
}