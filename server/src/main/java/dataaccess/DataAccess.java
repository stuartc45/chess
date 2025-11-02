package dataaccess;

import datamodel.*;

import java.sql.SQLException;
import java.util.*;

public interface DataAccess {
    void clear() throws DataAccessException;
    void createUser(UserData user) throws DataAccessException, SQLException;
    UserData getUser(String username) throws DataAccessException, SQLException;
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
    void addAuth(AuthData authData) throws DataAccessException;
    int createGame(GameData gameData) throws DataAccessException;
    GameData getGame(Integer gameID) throws DataAccessException;
    void updateGame(Integer gameID, String whiteUsername, String blackUsername, String gameName) throws DataAccessException;
    List<GameData> getGameList() throws DataAccessException, SQLException;
}
