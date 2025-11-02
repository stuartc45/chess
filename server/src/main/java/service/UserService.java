package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import datamodel.*;
import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;

public class UserService {
    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess=dataAccess;
    }

    public void clear() throws DataAccessException {
        dataAccess.clear();
    }

    public AuthData register(UserData user) throws Exception {
        if (dataAccess.getUser(user.username()) != null ) {
          throw new Exception("already taken");
        }
        if (user.username() == null || user.email() == null || user.password() == null) {
            throw new Exception("bad request");
        }
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        UserData tempUser = new UserData(user.username(), hashedPassword, user.email());
        dataAccess.createUser(tempUser);
        AuthData authData = new AuthData(user.username(), generateAuthToken());
        dataAccess.addAuth(authData);
        return authData;
    }

    public AuthData login(UserData user) throws Exception {
        if (user.username() == null || user.password() == null) {
            throw new Exception("bad request");
        }
        if (dataAccess.getUser(user.username()) == null) {
            throw new Exception("unauthorized");
        }
        UserData userData = dataAccess.getUser(user.username());
        if (!BCrypt.checkpw(user.password(), userData.password())) {
            throw new Exception("unauthorized");
        }
        AuthData authData = new AuthData(user.username(), generateAuthToken());
        dataAccess.addAuth(authData);
        return authData;
    }

    public void logout(String authToken) throws Exception {
        if (authToken == null) {
            throw new Exception("unauthorized");
        }
        if (dataAccess.getAuth(authToken) == null) {
            throw new Exception("unauthorized");
        }
        dataAccess.deleteAuth(authToken);
    }

    // use the script they gave you to generate the authToken
    public static String generateAuthToken() {
        return UUID.randomUUID().toString();
    }
}
