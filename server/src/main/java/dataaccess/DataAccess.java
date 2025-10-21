package dataaccess;

import datamodel.*;

public interface DataAccess {
    void clear();
    void createUser(UserData user);
    UserData getUser(String username);
    AuthData getAuth(String authToken);
    void deleteAuth(String authToken);
    void addAuth(AuthData authData);
}
