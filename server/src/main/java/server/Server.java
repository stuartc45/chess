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
        server.delete("db", context -> context.result("{}"));
        server.post("user", context -> register(context));

    }

    private void register(Context context) {
      try {
//      make sure you change the context type to be a javalin context once javalin is working
        var serializer=new Gson();
        String reqJson=context.body();
        var user=serializer.fromJson(reqJson, UserData.class);

        // call to the service and register
        var authData=userService.register(user);
        context.result(serializer.toJson(authData));
      } catch (Exception ex) {
        var message = String.format("{\"message\": \"Error: %s\" })", ex.getMessage());
        context.status(403).result(message);
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
