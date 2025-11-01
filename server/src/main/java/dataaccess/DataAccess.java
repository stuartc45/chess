package dataaccess;

import datamodel.*;

import java.sql.SQLException;
import java.util.*;

public interface DataAccess {
    void clear() throws DataAccessException;
    void createUser(UserData user) throws DataAccessException, SQLException;
    UserData getUser(String username) throws DataAccessException, SQLException;
    AuthData getAuth(String authToken);
    void deleteAuth(String authToken);
    void addAuth(AuthData authData) throws DataAccessException;
    void createGame(GameData gameData) throws DataAccessException;
    GameData getGame(Integer gameID);
    void updateGame(Integer gameID, String whiteUsername, String blackUsername, String gameName);
    List<GameData> getGameList();
}
