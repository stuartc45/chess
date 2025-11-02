package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import datamodel.AuthData;
import datamodel.GameData;
import datamodel.UserData;

import java.sql.*;
import java.util.ArrayList;
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
    public UserData getUser(String username) throws DataAccessException, SQLException {
        var statement = "SELECT * FROM user_data WHERE username = ?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, username);
                var rs = preparedStatement.executeQuery();
                UserData user = null;
                if (rs.next()) {
                    user = new UserData(rs.getString(1), rs.getString(2), rs.getString(3));
                }
                return user;
            }
        }
        catch (SQLException e) {
            throw new DataAccessException("failed");
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        var statement = "SELECT * FROM auth_data WHERE authToken = ?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, authToken);
                var rs = preparedStatement.executeQuery();
                AuthData auth = null;
                if (rs.next()) {
                    auth = new AuthData(rs.getString(1), rs.getString(2));
                }
                return auth;
            }
        }
        catch (SQLException | DataAccessException e) {
            throw new DataAccessException("failed");
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        var statement = "DELETE FROM auth_data WHERE authToken = ?";
        executeUpdate(statement, authToken);
    }

    @Override
    public void addAuth(AuthData authData) throws DataAccessException {
        var statement = "INSERT INTO auth_data (username, authToken) VALUES (?, ?)";
        executeUpdate(statement, authData.username(), authData.authToken());
    }

    @Override
    public void createGame(GameData gameData) throws DataAccessException {
        var statement = "INSERT INTO game_data (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
        executeUpdate(statement, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), gameData.game());
    }

    @Override
    public GameData getGame(Integer gameID) throws DataAccessException {
        var statement = "SELECT * FROM game_data WHERE gameID = ?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setInt(1, gameID);
                var rs = preparedStatement.executeQuery();
                GameData gameData = null;
                if (rs.next()) {
                    gameData = new GameData(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), new Gson().fromJson(rs.getString(5), ChessGame.class));
                }
                return gameData;
            }
        }
        catch (SQLException | DataAccessException e) {
            throw new DataAccessException("failed");
        }
    }

    @Override
    public void updateGame(Integer gameID, String whiteUsername, String blackUsername, String gameName) throws DataAccessException {
        var statement = "UPDATE game_data SET whiteUsername = ?, blackUsername = ?, gameName = ? WHERE gameID = ?;";
        executeUpdate(statement, whiteUsername, blackUsername, gameName, gameID);
    }

    @Override
    public List<GameData> getGameList() throws DataAccessException, SQLException {
        var statement = "SELECT * FROM game_data;";
        List<GameData> gameList = new ArrayList<>();
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                var rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    GameData game = new GameData(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), new Gson().fromJson(rs.getString(5), ChessGame.class));
                    gameList.add(game);
                }
            }
        }
        return gameList;
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
