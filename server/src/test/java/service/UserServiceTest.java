package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import datamodel.GameData;
import datamodel.UserData;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Test
    void register() throws Exception {
        DataAccess db = new MemoryDataAccess();
        var user = new UserData("joe", "j@j.com", "toomanysecrets");
        var userService = new UserService(db);
        var authData = userService.register(user);
        assertNotNull(authData);
        assertEquals(user.username(), authData.username());
        assertFalse(authData.authToken().isEmpty());
    }

    @Test
    void registerInvalidUsername() {
        DataAccess db = new MemoryDataAccess();
        var user = new UserData(null, "j@j.com", "toomanysecrets");
        var userService = new UserService(db);
        assertThrows(Exception.class, () -> userService.register(user));
    }

    @Test
    void login() throws Exception {
        DataAccess db = new MemoryDataAccess();
        var userService = new UserService(db);
        var user = new UserData("joe", "j@j.com", "toomanysecrets");
        db.createUser(user);
        var authData = userService.login(user);
        assertNotNull(authData);
        assertEquals(user.username(), authData.username());
        assertFalse(authData.authToken().isEmpty());
    }

    @Test
    void loginInvalidUsername() throws Exception {
        DataAccess db = new MemoryDataAccess();
        var userService = new UserService(db);
        var user = new UserData(null, "j@j.com", "toomanysecrets");
        db.createUser(user);
        assertThrows(Exception.class, () -> userService.login(user));
    }

    @Test
    void loginInvalidPassword() throws Exception {
        DataAccess db = new MemoryDataAccess();
        var userService = new UserService(db);
        var user = new UserData("joe", "j@j.com", "toomanysecrets");
        var badPassword = new UserData("joe", "j@j.com", "wrongPassword");
        db.createUser(user);
        assertThrows(Exception.class, () -> userService.login(badPassword));
    }

    @Test
    void logout() throws Exception {
        DataAccess db = new MemoryDataAccess();
        var userService = new UserService(db);
        var user = new UserData("joe", "j@j.com", "toomanysecrets");
        db.createUser(user);
        var authData = userService.login(user);
        userService.logout(authData.authToken());
        assertNull(db.getAuth(authData.authToken()));
    }

    @Test
    void doubleLogout() throws Exception {
        DataAccess db = new MemoryDataAccess();
        var userService = new UserService(db);
        var user = new UserData("joe", "j@j.com", "toomanysecrets");
        db.createUser(user);
        var authData = userService.login(user);
        userService.logout(authData.authToken());
        assertThrows(Exception.class, () -> userService.logout(authData.authToken()));
    }

    @Test
    void clear() throws Exception {
        DataAccess db = new MemoryDataAccess();
        var userService = new UserService(db);
        var user = new UserData("joe", "j@j.com", "toomanysecrets");
        var otherUser = new UserData("Jane", "jan@j.com", "notenoughsecrets");
        db.createUser(user);
        db.createUser(otherUser);
        var authData = userService.login(user);
        var otherData = userService.login(otherUser);
        userService.clear();
        assertNull(db.getUser("joe"));
        assertNull(db.getAuth(otherData.authToken()));
    }
}