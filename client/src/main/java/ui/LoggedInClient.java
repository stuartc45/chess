package ui;

import server.ServerFacade;

public class LoggedInClient {
    private final ServerFacade serverFacade;

    public LoggedInClient(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
    }
}
