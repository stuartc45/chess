package server;

import dataaccess.MemoryDataAccess;
import datamodel.*;
import io.javalin.*;
import io.javalin.http.Context;
import service.GameService;
import service.UserService;
import com.google.gson.Gson;

import java.util.*;

public class Server {

    private final Javalin server;
    private final UserService userService;
    private final GameService gameService;

    public Server() {
        var dataAccess = new MemoryDataAccess();
        userService = new UserService(dataAccess);
        gameService = new GameService(dataAccess);
        server = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        server.delete("/db", this::clear);
        server.post("/user", this::register);
        server.post("/session", this::login);
        server.delete("/session", this::logout);
        server.get("/game", this::listGames);
        server.post("/game", this::createGame);
        server.put("/game", this::joinGame);
    }

    private void clear(Context context) {
        userService.clear();
        context.result("{}");
    }

    private void register(Context context) {
        var serializer=new Gson();
        try {
            String reqJson=context.body();
            var user=serializer.fromJson(reqJson, UserData.class);

            // call to the service and register
            var authData=userService.register(user);
            context.result(serializer.toJson(authData));
        } catch (Exception ex) {
            var message = String.format("{\"message\": \"Error: %s\" }", ex.getMessage());
            if (ex.getMessage().equals("already taken")) {
                context.status(403).result(message);
            }
            else {
                context.status(400).result(message);
            }
        }
    }

    private void login(Context context) {
        var serializer = new Gson();
        try {
            String reqJson = context.body();
            var user = serializer.fromJson(reqJson, UserData.class);

            var authData = userService.login(user);
            context.result(serializer.toJson(authData));
        } catch (Exception ex) {
            var message = String.format("{\"message\": \"Error: %s\" }", ex.getMessage());
            if (ex.getMessage().equals("unauthorized")) {
                context.status(401).result(message);
            }
            else {
                context.status(400).result(message);
            }
        }
    }

    private void logout(Context context) {
        try {
            var data = context.header("authorization");

            userService.logout(data);
            context.status(200).result("{}");
        } catch (Exception ex) {
            catchException(ex, context);
        }
    }

    private void listGames(Context context) {
        try {
            var serializer = new Gson();
            var data = context.header("authorization");

            List<GameData> gameList = gameService.listGames(data);
            var returnData = String.format("{ \"games\": %s }", serializer.toJson(gameList));
            context.result(returnData);
        } catch (Exception ex) {
            catchException(ex, context);
        }
    }

    private void createGame(Context context) {
        var serializer = new Gson();
        try {
            var header = context.header("authorization");
            String reqJson = context.body();
            var data = serializer.fromJson(reqJson, GameData.class);

            int gameID = gameService.createGame(header, data);
            var returnData = String.format("{\"gameID\": %d }", gameID);
            context.result(returnData);
        } catch (Exception ex) {
            catchException(ex, context);
        }
    }

    private void joinGame(Context context) {
        var serializer = new Gson();
        try {
            var header = context.header("authorization");
            String reqJson = context.body();
            var data = serializer.fromJson(reqJson, JoinGameData.class);

            gameService.joinGame(header, data);
            context.result("{}");
        } catch (Exception ex) {
            var message = String.format("{\"message\": \"Error: %s\" }", ex.getMessage());
            if (ex.getMessage().equals("unauthorized")) {
                context.status(401).result(message);
            }
            else if (ex.getMessage().equals("already taken")) {
                context.status(403).result(message);
            }
            else {
                context.status(400).result(message);
            }
        }
    }

    private void catchException(Exception ex, Context context) {
        var message = String.format("{\"message\": \"Error: %s\" }", ex.getMessage());
        if (ex.getMessage().equals("unauthorized")) {
            context.status(401).result(message);
        }
        else {
            context.status(400).result(message);
        }
    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
