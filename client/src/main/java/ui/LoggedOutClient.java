package ui;

import server.ServerFacade;

import java.util.Scanner;
import static ui.EscapeSequences.*;

public class LoggedOutClient {
    private final ServerFacade server;

    public LoggedOutClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to chess! Type \"help\" to get help!");
        printPrompt();
        
        var result = "";
        String line = scanner.nextLine();
        result = eval(line);
    }

    private String eval(String input) {
        if (input.equals("help")) {
            help();
        }
        return "";
    }

    private void printPrompt() {
        System.out.print("\n" + SET_TEXT_COLOR_BLACK + ">>> " + SET_TEXT_COLOR_GREEN);
    }

    private void help() {
        System.out.println("register <USERNAME> <PASSWORD> <EMAIL> - to create an account");
        System.out.println("login <USERNAME> <PASSWORD> - to log in");
        System.out.println("quit - to exit the program");
        System.out.println("help - to print possible commands");
    }
}
