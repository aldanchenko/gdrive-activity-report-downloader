package pt.outsystems.gdrive.downloader;

import org.junit.Test;
import pt.outsystems.gdrive.downloader.jws.AccessToken;

import java.io.*;
import java.security.*;

import static org.junit.Assert.assertNotNull;

/**
 * Test case for GDriveActivityReportDownloader.
 */
public class GDriveActivityReportDownloaderTest {

    @Test
    public void testReadAccountServiceCredentials() throws FileNotFoundException {
        GDriveActivityReportDownloader reportDownloader = new GDriveActivityReportDownloader();

        ServiceAccountCredentials serviceAccountCredentials = reportDownloader.loadCredentials("");

        assertNotNull(serviceAccountCredentials);
    }

    @Test
    public void testGetAccessToken() throws IOException, GeneralSecurityException {
        GDriveActivityReportDownloader reportDownloader = new GDriveActivityReportDownloader();

        ServiceAccountCredentials serviceAccountCredentials = reportDownloader.loadCredentials("");

        AccessToken accessToken = reportDownloader.authorize(serviceAccountCredentials);

        assertNotNull(accessToken);
    }
}
