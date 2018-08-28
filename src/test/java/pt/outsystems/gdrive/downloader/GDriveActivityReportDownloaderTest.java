package pt.outsystems.gdrive.downloader;

import org.junit.Test;
import pt.outsystems.gdrive.downloader.domain.ServiceAccountCredentials;
import pt.outsystems.gdrive.downloader.jws.AccessToken;

import java.io.*;
import java.security.*;

import static org.junit.Assert.assertNotNull;

/**
 * Test case for GDriveActivityReportDownloader.
 */
public class GDriveActivityReportDownloaderTest {

    /**
     * File path to Service Account JSON for tests.
     */
    private String jsonFilePathStr = "";

    @Test
    public void testReadAccountServiceCredentials() throws FileNotFoundException {
        GDriveActivityReportDownloader reportDownloader = new GDriveActivityReportDownloader();

        ServiceAccountCredentials serviceAccountCredentials = reportDownloader.loadCredentials(jsonFilePathStr);

        assertNotNull(serviceAccountCredentials);
    }

    @Test
    public void testAuthorize() throws IOException, GeneralSecurityException {
        GDriveActivityReportDownloader reportDownloader = new GDriveActivityReportDownloader();

        ServiceAccountCredentials serviceAccountCredentials = reportDownloader.loadCredentials(jsonFilePathStr);

        AccessToken accessToken = reportDownloader.authorize(serviceAccountCredentials);

        assertNotNull(accessToken);
    }

    @Test
    public void testGetReports() throws IOException, GeneralSecurityException {
        GDriveActivityReportDownloader reportDownloader = new GDriveActivityReportDownloader();

        ServiceAccountCredentials serviceAccountCredentials = reportDownloader.loadCredentials(jsonFilePathStr);

        AccessToken accessToken = reportDownloader.authorize(serviceAccountCredentials);

        assertNotNull(accessToken);

        reportDownloader.loadReports(accessToken, "2018-08-01", "2018-10-01");
    }
}
