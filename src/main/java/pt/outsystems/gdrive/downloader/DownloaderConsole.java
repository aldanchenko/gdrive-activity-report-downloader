package pt.outsystems.gdrive.downloader;

import java.util.*;

/**
 * Since Java 1.6+ have {@link java.io.Console} class, but this class not work with javaw.exe (javaw.sh) this class implements
 * similar functions using {@link java.util.Scanner}.
 */
public class DownloaderConsole {

    /**
     * Singleton console instance.
     */
    private static DownloaderConsole consoleInstance;

    /**
     * Initialize scanner object with System input stream.
     */
    private Scanner scanner = new Scanner(System.in);

    /**
     * Default private constructor for singleton console.
     */
    private DownloaderConsole() {
    }

    /**
     * Show message to user in console and wait user input string.
     *
     * @param message   - message to user
     *
     * @return String
     */
    public String readLine(String message) {
        System.out.print(message + ": ");

        return scanner.nextLine();
    }

    /**
     * Check is instance already created and if not create it. At the end return singleton.
     *
     * @return DownloaderConsole
     */
    public static DownloaderConsole getInstance() {
        if (consoleInstance == null) {
            consoleInstance = new DownloaderConsole();
        }

        return consoleInstance;
    }

    /**
     * Print console hello message.
     */
    public void printHelloMessage() {
        System.out.println("/----------------------------------------------------------------------");
        System.out.println("--------------------------GitHub Integration---------------------------");
        System.out.println("----------------------------------------------------------------------/");
    }

    /**
     * Print available by this application commands.
     */
    public void printAvailableCommandsInformation() {
        System.out.println("Available commands:");
        System.out.println("        * query");
        System.out.println("        * fork");
        System.out.println("        * test");
        System.out.println("        * version");
    }

    /**
     * Start console commands loop.
     */
    public void runCommandsLoop() {

        boolean isNotExitCommand;

        do {
            String command = this.readLine("Please, enter command");

            command = command.trim();

            isNotExitCommand = !isExitCommand(command);

            if (isNotExitCommand) {
                if ("".equalsIgnoreCase(command)) {
                } else if ("".equalsIgnoreCase(command)) {
                    System.err.println("Not implemented command");
                } else if ("".equalsIgnoreCase(command)) {
                    System.err.println("Not implemented command");
                } else if ("".equalsIgnoreCase(command)) {
                    System.out.println("Current Version: " + "1123");
                }
            }
        } while (isNotExitCommand);

        System.out.println("Bye!");
    }

    /**
     * Show error message and exit application.
     *
     * @param exception - source exception for get description message
     */
    public void errorExit(Exception exception) {
        errorExit(exception.getMessage());
    }

    /**
     * Show error message and exit application.
     *
     * @param errorMessage - source description error message
     */
    public void errorExit(String errorMessage) {
        System.err.println("Error message: " + errorMessage);
        System.err.println("Bye.");

        System.exit(-1);
    }

    /**
     * Check is command equals to 'exit'.
     *
     * @param command - source command string
     *
     * @return String
     */
    private boolean isExitCommand(String command) {
        return "exit".equalsIgnoreCase(command);
    }
}
