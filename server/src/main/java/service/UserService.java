package service;

import dataaccess.DataAccess;
import datamodel.*;

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
        UserData userData = dataAccess.getUser(user.username());
        if (userData.username() == null) {
            throw new Exception("unauthorized");
        }
        if (user.username() == null || user.password() == null) {
            throw new Exception("bad request");
        }
        if (!user.password().equals(userData.password())) {
            throw new Exception("unauthorized");
        }
        return new AuthData(user.username(), generateAuthToken());
    }

    // use the script they gave you to generate the authToken
    private String generateAuthToken() {
        return "xyz";
    }
}
