package server;

import dataaccess.MemoryDataAccess;
import datamodel.*;
import io.javalin.*;
import io.javalin.http.Context;
import service.UserService;
import com.google.gson.Gson;

public class Server {

    private final Javalin server;
    private final UserService userService;

    public Server() {
        var dataAccess = new MemoryDataAccess();
        userService = new UserService(dataAccess);
        server = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        server.delete("/db", context -> context.result("{}"));
        server.post("/user", this::register);
        server.post("/session", this::login);
        server.delete("/session", this::logout);
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
            if (ex.getMessage().equals("already exists")) {
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
        var serializer = new Gson();
        try {
            String reqJson = context.body();
            var user = serializer.fromJson(reqJson, UserData.class);

            
        } catch (Exception ex) {

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
