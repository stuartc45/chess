package dataaccess;

import datamodel.*;
import java.util.*;

public interface DataAccess {
    void clear();
    void createUser(UserData user);
    UserData getUser(String username);
    AuthData getAuth(String authToken);
    void deleteAuth(String authToken);
    void addAuth(AuthData authData);
    void createGame(GameData gameData);
    GameData getGame(Integer gameID);
    void updateGame(Integer gameID, String whiteUsername, String blackUsername, String gameName);
    List<GameData> getGameList();
}
