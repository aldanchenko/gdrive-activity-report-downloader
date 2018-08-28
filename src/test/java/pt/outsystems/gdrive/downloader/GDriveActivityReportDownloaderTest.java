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

        ServiceAccountCredentials serviceAccountCredentials = reportDownloader.loadCredentials("/home/winter/Downloads/golden-plateau-191208-bcd6fb4e3a19.json");

        assertNotNull(serviceAccountCredentials);
    }

    @Test
    public void testGetAccessToken() throws IOException, GeneralSecurityException {
        GDriveActivityReportDownloader reportDownloader = new GDriveActivityReportDownloader();

        ServiceAccountCredentials serviceAccountCredentials = reportDownloader.loadCredentials("/home/winter/Downloads/golden-plateau-191208-c1754842687b.json");

        AccessToken accessToken = reportDownloader.authorize(serviceAccountCredentials);

        assertNotNull(accessToken);
    }

    /*@Test
    public void testViaRunGoogleLibrary() throws Exception {
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream("/home/winter/Downloads/golden-plateau-191208-bcd6fb4e3a19.json"));

        List<String> driveReportsScopes = Arrays.asList("https://www.googleapis.com/auth/admin.reports.audit.readonly",
                "https://www.googleapis.com/auth/admin.reports.usage.readonly");

        List<String> driveScopes = Arrays.asList("https://www.googleapis.com/auth/drive",
                "https://www.googleapis.com/auth/drive.appdata",
                "https://www.googleapis.com/auth/drive.file",
                "https://www.googleapis.com/auth/drive.metadata",
                "https://www.googleapis.com/auth/drive.metadata.readonly",
                "https://www.googleapis.com/auth/drive.photos.readonly",
                "https://www.googleapis.com/autрашh/drive.readonly");

        List<String> scopes = new ArrayList<>();

        scopes.addAll(driveReportsScopes);

        ServiceAccountCredentials newCredentials =
                (ServiceAccountCredentials) credentials.createScoped(scopes);

        newCredentials.refreshIfExpired();

        AccessToken accessToken = newCredentials.getAccessToken();

        System.out.println(accessToken.getTokenValue());
    }*/

    /*private static void sendGet(String accessTokenStr) throws Exception {
//        String urlString = "https://www.googleapis.com/drive/v2/files?access_token=" + accessTokenStr;
//        String urlString = "https://www.googleapis.com/drive/v2/files";
        String urlString = "https://www.googleapis.com/admin/reports/v1/activity/users/all/applications/drive";
//        String urlString = "https://www.googleapis.com/drive/v3/files?access_token=" + accessTokenStr;
//        String urlString = "https://www.googleapis.com/drive/v3/files";

        URL url = new URL(urlString);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        // optional default is GET
        urlConnection.setRequestMethod("GET");
        urlConnection.setDoInput(true);

        // Add request header.
        urlConnection.setRequestProperty("Authorization", "Bearer " + accessTokenStr);

        int responseCode = urlConnection.getResponseCode();

        System.out.println("\nSending 'GET' request to URL : " + urlString);
        System.out.println("Response Code : " + responseCode);

        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(urlConnection.getInputStream()));

        String inputLine;
        StringBuffer stringBuffer = new StringBuffer();

        while ((inputLine = bufferedReader.readLine()) != null) {
            stringBuffer.append(inputLine);
        }

        bufferedReader.close();

        //print result
        System.out.println(stringBuffer.toString());

    }*/
}
