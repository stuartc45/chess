package dataaccess;

import chess.ChessGame;
import datamodel.*;
import org.junit.jupiter.api.Test;
import service.GameService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SqlDataAccessTest {

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
    void getUser() {
    }

    @Test
    void getAuth() {
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
    void addAuth() throws DataAccessException {
        DataAccess db = new SqlDataAccess();
        var authData = new AuthData("joe", "xyz");
        db.addAuth(authData);
        assertEquals(authData, db.getAuth(authData.authToken()));
    }

    @Test
    void createGame() throws DataAccessException {
        DataAccess db = new SqlDataAccess();
        var gameData = new GameData(1, null, null, "game", new ChessGame());
        db.createGame(gameData);
        assertEquals(gameData, db.getGame(1));
    }

    @Test
    void getGame() {
    }

    @Test
    void updateGame() throws DataAccessException {
        DataAccess db = new SqlDataAccess();
        var gameData = new GameData(1, null, null, "game", new ChessGame());
        db.createGame(gameData);
        db.updateGame(1, "player1", null, "game");
        var game = db.getGame(1);
        assertEquals(game.whiteUsername(), "player1");
        assertEquals(game.blackUsername(), gameData.blackUsername());
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
        assertEquals(gameList, listOfGames);
    }
}