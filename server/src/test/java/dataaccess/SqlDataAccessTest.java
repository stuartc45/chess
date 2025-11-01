package dataaccess;

import chess.ChessGame;
import datamodel.*;
import org.junit.jupiter.api.Test;
import service.GameService;

import java.sql.SQLException;

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
        var user = new UserData("joe", "j@j.com", "toomanysecrets");
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
    void updateGame() {
    }

    @Test
    void getGameList() {
    }
}