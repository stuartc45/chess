package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import datamodel.AuthData;
import datamodel.GameData;
import datamodel.UserData;

import java.sql.*;
import java.util.List;

import static java.sql.Types.NULL;

public class SqlDataAccess implements DataAccess {
    public SqlDataAccess() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public void clear() throws DataAccessException {
        var statements = new String[] {
                "DELETE FROM auth_data",
                "DELETE FROM user_data",
                "DELETE FROM game_data"
        };
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String str : statements)
                try (var preparedStatement = conn.prepareStatement(str)) {
                    preparedStatement.executeUpdate();
                }
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException("failed");
        }
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        var statement = "INSERT INTO user_data (username, password, email) VALUES (?, ?, ?)";
        executeUpdate(statement, user.username(), user.password(), user.email());
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
//        var statement = "SELECT * FROM user_data WHERE username = ?";
//        executeUpdate(statement, username);
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
    public void addAuth(AuthData authData) throws DataAccessException {
        var statement = "INSERT INTO auth_data (username, authToken) VALUES (?, ?)";
        executeUpdate(statement, authData.username(), authData.authToken());
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

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement str = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String s) str.setString(i + 1, s);
                    else if (param instanceof ChessGame s) str.setString(i + 1, new Gson().toJson(s));
                    else if (param instanceof Integer s) str.setInt(i + 1, s);
                    else if (param == null) str.setNull(i + 1, NULL);
                }
                str.executeUpdate();

                ResultSet rs = str.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException("failed");
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS user_data (
                `username` VARCHAR(255) NOT NULL,
                `password` VARCHAR(255) NOT NULL,
                `email` VARCHAR(255) NOT NULL UNIQUE,
                PRIMARY KEY (`username`)
            );
            """,
            """
            CREATE TABLE IF NOT EXISTS auth_data (
                `username` VARCHAR(255) NOT NULL,
                `authToken` VARCHAR(255) NOT NULL,
                PRIMARY KEY (`authToken`)
            );
            """,
            """
            CREATE TABLE IF NOT EXISTS game_data (
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
