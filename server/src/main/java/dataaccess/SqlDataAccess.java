package dataaccess;

import datamodel.AuthData;
import datamodel.GameData;
import datamodel.UserData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class SqlDataAccess implements DataAccess {
    public SqlDataAccess() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public void clear() {

    }

    @Override
    public void createUser(UserData user) {

    }

    @Override
    public UserData getUser(String username) {
        return null;
    }

    @Override
    public AuthData getAuth(String authToken) {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) {

    }

    @Override
    public void addAuth(AuthData authData) {

    }

    @Override
    public void createGame(GameData gameData) {

    }

    @Override
    public GameData getGame(Integer gameID) {
        return null;
    }

    @Override
    public void updateGame(Integer gameID, String whiteUsername, String blackUsername, String gameName) {

    }

    @Override
    public List<GameData> getGameList() {
        return List.of();
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS UserData (
                `username` VARCHAR(255) NOT NULL,
                `password` VARCHAR(255) NOT NULL,
                `email` VARCHAR(255) NOT NULL UNIQUE,
                PRIMARY KEY (`username`)
            );
            """,
            """
            CREATE TABLE IF NOT EXISTS AuthData (
                `username` VARCHAR(255) NOT NULL,
                `authToken` VARCHAR(255) NOT NULL,
                PRIMARY KEY (`authToken`)
            );
            """,
            """
            CREATE TABLE IF NOT EXISTS GameData (
                `gameID` INT NOT NULL AUTO_INCREMENT,
                `whiteUsername` VARCHAR(255),
                `blackUsername` VARCHAR(255),
                `gameName` VARCHAR (255) NOT NULL,
                `game` LONGTEXT NOT NULL,
                PRIMARY KEY (`gameID`)
            );
            """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String str : createStatements) {
                try (var preparedStatement = conn.prepareStatement(str)) {
                    preparedStatement.executeUpdate();
                }
            }

        } catch (SQLException ex) {
            throw new DataAccessException("Failed");
        }
    }
}
