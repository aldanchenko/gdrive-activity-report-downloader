package pt.outsystems.gdrive.downloader;

/**
 * Application entry point class.
 */
public class GDriveActivityReportDownloaderApplication {

    /**
     * Entry point method.
     *
     * @param parameters - console parameters
     */
    public static void main(String[] parameters) {
        DownloaderConsole downloaderConsole = DownloaderConsole.getInstance();

        downloaderConsole.printHelloMessage();
        downloaderConsole.printAvailableCommandsInformation();

        downloaderConsole.runCommandsLoop();
    }
}
