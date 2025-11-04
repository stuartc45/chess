package dataaccess;

import chess.ChessGame;
import datamodel.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        var user2 = new UserData("frank", "nopass", "f@n.com");
        db.createUser(user);
        db.createUser(user2);
        assertEquals(user, db.getUser(user.username()));
        assertEquals(user2, db.getUser(user2.username()));
    }

    @Test
    void createBadUser() throws SQLException, DataAccessException {
        DataAccess db = new SqlDataAccess();
        var user = new UserData(null, "toomanysecrets", "j@j.com");
        assertThrows(Exception.class, () -> db.createUser(user));
    }

    @Test
    void getUser() throws DataAccessException, SQLException {
        DataAccess db = new SqlDataAccess();
        UserData userData = new UserData("bill", "billyboy", "b@b.org");
        db.createUser(userData);
        assertEquals(userData, db.getUser(userData.username()));
    }

    @Test
    void getUserBad() throws DataAccessException, SQLException {
        DataAccess db = new SqlDataAccess();
        var user = new UserData("fred", "pass", "fred@.com");
        assertNull(db.getUser(user.username()));
    }

    @Test
    void getAuth() throws DataAccessException {
        DataAccess db = new SqlDataAccess();
        var authData = new AuthData("george", "xyz");
        db.addAuth(authData);
        assertEquals(authData, db.getAuth(authData.authToken()));
    }

    @Test
    void getAuthBad() throws DataAccessException {
        DataAccess db = new SqlDataAccess();
        var authData = new AuthData("harry", "potter");
        assertNull(db.getAuth(authData.authToken()));
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
    void deleteAuthBad() throws DataAccessException {
        DataAccess db = new SqlDataAccess();
        var authData = new AuthData("Ginny", "Weasley");
        db.addAuth(authData);
        db.deleteAuth("waesley");
        assertEquals(authData, db.getAuth(authData.authToken()));
    }

    @Test
    void addAuth() throws DataAccessException {
        DataAccess db = new SqlDataAccess();
        var authData = new AuthData("joe", "xyz");
        var auth2 = new AuthData("Ron", "Weasley");
        db.addAuth(authData);
        db.addAuth(auth2);
        assertEquals(authData, db.getAuth(authData.authToken()));
        assertEquals(auth2, db.getAuth(auth2.authToken()));
    }

    @Test
    void addAuthBad() throws DataAccessException {
        DataAccess db = new SqlDataAccess();
        var auth = new AuthData("blank", null);
        assertThrows(Exception.class, () -> db.addAuth(auth));
    }

    @Test
    void createGame() throws DataAccessException {
        DataAccess db = new SqlDataAccess();
        var gameData = new GameData(1, null, null, "gameData", new ChessGame());
        var gameData2 = new GameData(2, null, null, "gameData2", new ChessGame());
        db.createGame(gameData);
        db.createGame(gameData2);
        assertEquals(gameData, db.getGame(1));
        assertEquals(gameData2, db.getGame(2));
    }

    @Test
    void createBadGame() throws DataAccessException {
        DataAccess db = new SqlDataAccess();
        var game1 = new GameData(1, null, null, "game1", new ChessGame());
        var game2 = new GameData(2, null, null, "game2", new ChessGame());
        db.createGame(game1);
        db.createGame(game2);
        assertNotEquals(game1, db.getGame(3));
        assertNotEquals(game2, db.getGame(4));
    }

    @Test
    void getGame() throws DataAccessException {
        DataAccess db = new SqlDataAccess();
        GameData game = new GameData(1, null, null, "game", new ChessGame());
        db.createGame(game);
        assertEquals(game, db.getGame(game.gameID()));
    }

    @Test
    void getGameBad() throws DataAccessException {
        DataAccess db = new SqlDataAccess();
        var gameData = new GameData(1, null, null, "game6", new ChessGame());
        db.createGame(gameData);
        assertNull(db.getGame(100));
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
    void updateGameBad() throws DataAccessException {
        DataAccess db = new SqlDataAccess();
        var gameData = new GameData(1, null, null, "game8", new ChessGame());
        db.createGame(gameData);
        db.updateGame(1, "white", null, "game");
        assertThrows(Exception.class, () -> db.updateGame(1, "white", "black", null));

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
    void getGameListBad() throws DataAccessException, SQLException {
        DataAccess db = new SqlDataAccess();
        var emptyGame = new GameData(1, null, null, "empty", new ChessGame());
        var badGame = new GameData(2, "green", "yellow", "badGame", new ChessGame());
        var otherGame = new GameData(3, null, null, "other", new ChessGame());
        db.createGame(emptyGame);
        db.createGame(badGame);
        db.createGame(otherGame);
        List<GameData> listGames = new ArrayList<>();
        listGames.add(emptyGame);
        listGames.add(badGame);
        var gameList = db.getGameList();
        assertNotEquals(listGames, gameList);
    }
}