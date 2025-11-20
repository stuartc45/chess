package ui;

import serverfacade.ServerFacade;

public class LoggedInClient {
    private final ServerFacade serverFacade;

    public LoggedInClient(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
    }
}
