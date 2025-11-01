package dataaccess;

import chess.ChessGame;
import datamodel.*;
import org.junit.jupiter.api.Test;
import service.GameService;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class SqlDataAccessTest {

    @Test
    void clear() throws DataAccessException {
        var dataAccess = new SqlDataAccess();

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
    void deleteAuth() {
    }

    @Test
    void addAuth() throws DataAccessException {
        DataAccess db = new SqlDataAccess();
        var authData = new AuthData("joe", "xyz");
        db.addAuth(authData);
    }

    @Test
    void createGame() throws DataAccessException {
        DataAccess db = new SqlDataAccess();
        var gameData = new GameData(null, null, null, "game", new ChessGame());
        db.createGame(gameData);
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