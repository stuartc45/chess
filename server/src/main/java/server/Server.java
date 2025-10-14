package server;

import io.javalin.*;

import java.util.Map;

public class Server {

    private final Javalin server;

    public Server() {
        server = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        server.delete("db", context -> context.result("{}"));
        server.post("user", context -> register(context));

    }

    private void register(Context context) {
//      make sure you change the context type to be a javalin context once javalin is working
        var serializer = new Gson();
        String reqJson = context.body();
        var req = serializer.fromJson(reqJson, Map.class);

        // call to the service and register

        var res = Map.of("username", req.get("username"), "authToken", "yzx");
        context.result(serializer.toJson(res));
    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
