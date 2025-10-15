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
        dataAccess.createUser(user);
        return new AuthData(user.username(), generateAuthToken());
    }


    // use the script they gave you to generate the authToken
    private String generateAuthToken() {
        return "xyz";
    }
}
