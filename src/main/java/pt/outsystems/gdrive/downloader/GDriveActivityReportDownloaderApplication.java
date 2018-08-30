package pt.outsystems.gdrive.downloader;

import pt.outsystems.gdrive.downloader.domain.ServiceAccountCredentials;
import pt.outsystems.gdrive.downloader.domain.report.Report;
import pt.outsystems.gdrive.downloader.jws.AccessToken;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Application entry point class.
 */
public class GDriveActivityReportDownloaderApplication {

    /**
     * Entry point method. This application require 3 input parameters.
     *  1. File path to Service account JSON.
     *  2. Start date for report data
     *  3. End date for report data
     *
     * @param parameters - console parameters
     */
    public static void main(String[] parameters) throws IOException, GeneralSecurityException {
        if (parameters.length != 3) {
            throw new IllegalArgumentException("Please, provide parameters (file path to JSON, start date, end date.");
        }

        String jsonFilePathStr = parameters[0];
        String startDateStr = parameters[1];
        String endDateStr = parameters[2];

        GDriveActivityReportDownloader reportDownloader = new GDriveActivityReportDownloader();

        ServiceAccountCredentials serviceAccountCredentials = reportDownloader.loadCredentials(jsonFilePathStr);

        AccessToken accessToken = reportDownloader.authorize(serviceAccountCredentials);

        Report report = reportDownloader.loadReports(accessToken, startDateStr, endDateStr);

        reportDownloader.saveReport("audit-report.csv", report);
    }
}
