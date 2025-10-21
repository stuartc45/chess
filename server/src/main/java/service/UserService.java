package service;

import dataaccess.DataAccess;
import datamodel.*;
import java.util.UUID;

public class UserService {
    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess=dataAccess;
    }

    public AuthData register(UserData user) throws Exception {
        if (dataAccess.getUser(user.username()) != null ) {
          throw new Exception("already exists");
        }
        if (user.username() == null || user.email() == null || user.password() == null) {
            throw new Exception("bad request");
        }
        dataAccess.createUser(user);
        return new AuthData(user.username(), generateAuthToken());
    }

    public AuthData login(UserData user) throws Exception {
        if (user.username() == null || user.password() == null) {
            throw new Exception("bad request");
        }
        if (dataAccess.getUser(user.username()) == null) {
            throw new Exception("unauthorized");
        }
        UserData userData = dataAccess.getUser(user.username());
        if (!user.password().equals(userData.password())) {
            throw new Exception("unauthorized");
        }
        return new AuthData(user.username(), generateAuthToken());
    }

    public void logout(AuthData authData) throws Exception {

    }

    // use the script they gave you to generate the authToken
    public static String generateAuthToken() {
        return UUID.randomUUID().toString();
    }
}
