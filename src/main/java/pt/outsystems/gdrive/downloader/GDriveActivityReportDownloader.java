package pt.outsystems.gdrive.downloader;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.opencsv.CSVWriter;
import pt.outsystems.gdrive.downloader.domain.ServiceAccountCredentials;
import pt.outsystems.gdrive.downloader.domain.report.Event;
import pt.outsystems.gdrive.downloader.domain.report.Item;
import pt.outsystems.gdrive.downloader.domain.report.Report;
import pt.outsystems.gdrive.downloader.jws.AccessToken;
import pt.outsystems.gdrive.downloader.jws.JwtHeader;
import pt.outsystems.gdrive.downloader.jws.JwtClaimSet;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;

/**
 * Google drive audit activity report downloader.
 */
public class GDriveActivityReportDownloader {

    /**
     * Default algorithm for {@link JwtHeader}. Default value is RS256.
     */
    private static final String DEFAULT_ALGORITHM = "RS256";

    /**
     * Default type for {@link JwtHeader}.
     */
    private static final String JWT_DEFAULT_TYPE = "JWT";

    /**
     * Default grant_type parameter value for authorization request.
     */
    private static final String DEFAULT_GRANT_TYPE = "urn:ietf:params:oauth:grant-type:jwt-bearer";

    /**
     * URL to Google Audit Activity report for drive application.
     */
    private static final String GOOGLE_DRIVE_ACTIVITY_REPORT_API_URL = "https://www.googleapis.com/admin/reports/v1/activity/users/all/applications/drive";

    /**
     * Contains requested scopes for Report API.
     */
    private List<String> scopes = Arrays.asList("https://www.googleapis.com/auth/admin.reports.audit.readonly",
            "https://www.googleapis.com/auth/admin.reports.usage.readonly");

    /**
     * Gson instance to work with json.
     */
    private Gson gson = new Gson();

    /**
     * Load service account credentials from json file to {@link ServiceAccountCredentials} object.
     *
     * @param fileName  - path to service account json file
     *
     * @throws FileNotFoundException -
     *
     * @return ServiceAccountCredentials
     */
    public ServiceAccountCredentials loadCredentials(String fileName) throws FileNotFoundException {
        Gson gson = new Gson();

        JsonReader jsonReader = new JsonReader(new FileReader(fileName));

        return gson.fromJson(jsonReader, ServiceAccountCredentials.class);
    }

    /**
     * Request Google audit activity reports.
     *
     * @param accessToken   - oauth access token
     * @param startDateStr  - start date to search data
     * @param endDateStr    - end date to search data
     *
     * @throws IOException -
     *
     * @return Report
     */
    public Report loadReports(AccessToken accessToken, String startDateStr, String endDateStr) throws IOException {
        String urlString = GOOGLE_DRIVE_ACTIVITY_REPORT_API_URL + "?start-date=" + startDateStr + "&end-date=" + endDateStr;

        URL url = new URL(urlString);
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

        // optional default is GET
        urlConnection.setRequestMethod("GET");
        urlConnection.setDoInput(true);

        // Add request header.
        urlConnection.setRequestProperty("Authorization", "Bearer " + accessToken.getValue());
        urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        urlConnection.setRequestProperty("User-Agent", "Google-HTTP-Java-Client/1.19.0 (gzip)");
        urlConnection.setRequestProperty("Accept-Charset", "UTF-8");

        int responseCode = urlConnection.getResponseCode();

        BufferedReader bufferedReader;

        if (responseCode == 200) {
            bufferedReader = new BufferedReader(
                    new InputStreamReader(urlConnection.getInputStream()));
        } else {
            bufferedReader = new BufferedReader(
                    new InputStreamReader(urlConnection.getErrorStream()));
        }

        String inputLine;
        StringBuffer stringBuffer = new StringBuffer();

        while ((inputLine = bufferedReader.readLine()) != null) {
            stringBuffer.append(inputLine);
        }

        bufferedReader.close();

        return gson.fromJson(stringBuffer.toString(), Report.class);
    }

    /**
     * Request access token.
     *
     * @param serviceAccountCredentials - service account credentials
     * @throws IOException                  -
     * @throws GeneralSecurityException     -
     *
     * @return AccessToken
     */
    public AccessToken authorize(ServiceAccountCredentials serviceAccountCredentials) throws IOException, GeneralSecurityException {
        if (Objects.isNull(serviceAccountCredentials)) {
            throw new IllegalArgumentException("Please, provide credentials.");
        }

        String jwt = buildJwt(serviceAccountCredentials);
        String signature = buildSignature(serviceAccountCredentials, jwt);

        String jsonWebSignature = jwt + "." + signature;

        byte[] postData = buildAuthorizationRequestParameters(jsonWebSignature).getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;

        String requestURL = serviceAccountCredentials.getTokenUri();

        URL url = new URL(requestURL);

        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        urlConnection.setRequestProperty("User-Agent", "Google-HTTP-Java-Client/1.19.0 (gzip)");
        urlConnection.setRequestProperty("Accept-Charset", "UTF-8");
        urlConnection.setRequestProperty("Content-Length", Integer.toString(postDataLength));
        urlConnection.setUseCaches(false);
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);

        try (DataOutputStream dataOutputStream = new DataOutputStream(urlConnection.getOutputStream())) {
            dataOutputStream.write(postData);
        }

        // Read response.
        StringBuilder responseStringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

        String line;
        while ( (line = bufferedReader.readLine()) != null)
            responseStringBuilder.append(line);

        // Close stream.
        bufferedReader.close();

        return gson.fromJson(responseStringBuilder.toString(), AccessToken.class);
    }

    /**
     * Save {@link Report} to CSV file.
     *
     * @param csvFileName   - CSV file path
     * @param report        - source report object
     *
     * @throws IOException  -
     */
    public void saveReport(String csvFileName, Report report) throws IOException {
        CSVWriter csvWriter = null;

        try {
            csvWriter = new CSVWriter(new FileWriter(csvFileName));

            String[] headers = {
                    "Kind",
                    "Time",
                    "Unique Qualifier",
                    "Application Name",
                    "Customer Id",
                    "Caller Type",
                    "Email",
                    "Profile Id",
                    "Key",
                    "Owner Domain",
                    "IP Address",
                    "Event type",
                    "Event edit",
                    "Event parameters",
            };

            csvWriter.writeNext(headers);

            for (Item item : report.getItems()) {
                String[] itemRowData = new String[11];

                itemRowData[0] = item.getKind();
                itemRowData[1] = item.getId().getTime();
                itemRowData[2] = item.getId().getUniqQualifier();
                itemRowData[3] = item.getId().getApplicationName();
                itemRowData[4] = item.getId().getCustomerId();
                itemRowData[5] = item.getActor().getCallerType();
                itemRowData[6] = item.getActor().getEmail();
                itemRowData[7] = item.getActor().getProfileId();
                itemRowData[8] = item.getActor().getKey();
                itemRowData[9] = item.getOwnerDomain();
                itemRowData[10] = item.getIpAddress();

                csvWriter.writeNext(itemRowData);

                for (Event event : item.getEvents()) {
                    String[] eventRowData = new String[headers.length];

                    // Fill item related cells with empty values.
                    for (int i = 0; i < 12; i++) {
                        eventRowData[i] = "";
                    }

                    eventRowData[11] = event.getType();
                    eventRowData[12] = event.getEdit();
                    eventRowData[13] = event.getParametersAsString();

                    csvWriter.writeNext(eventRowData);
                }
            }
        } catch (IOException ioException) {
            throw new IOException(ioException);
        } finally {
            if (csvWriter != null) {
                try {
                    csvWriter.close();
                } catch (IOException closeException) {
                    closeException.printStackTrace();
                }
            }
        }
    }

    /**
     * Build signature for Json web signature.
     *
     * @param serviceAccountCredentials - service account credentials
     * @throws UnsupportedEncodingException -
     * @throws GeneralSecurityException     -
     *
     * @return String
     */
    private String buildSignature(ServiceAccountCredentials serviceAccountCredentials, String jwtContentStr)
                            throws UnsupportedEncodingException, GeneralSecurityException {
        byte[] contentBytes = getUtf8Bytes(jwtContentStr);

        byte[] signature = signWithAlgorithm(getSha256WithRsaSignatureAlgorithm(),
                                        createRsaPrivateKey(serviceAccountCredentials.getPrivateKey()), contentBytes);

        return org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString(signature);
    }

    /**
     * Build JWT string using header and claim set.
     *
     * @param serviceAccountCredentials - service account credentials
     *
     * @return String
     */
    private String buildJwt(ServiceAccountCredentials serviceAccountCredentials) {
        JwtHeader jwtHeader = new JwtHeader.Builder()
                .algorithm(DEFAULT_ALGORITHM)
                .type(JWT_DEFAULT_TYPE)
                .keyId(serviceAccountCredentials.getPrivateKeyId())
                .build();

        long currentTime = System.currentTimeMillis();

        JwtClaimSet jwtClaimSet = new JwtClaimSet.Builder()
                .issuer(serviceAccountCredentials.getClientEmail())
                .issuedAtTimeSeconds(currentTime / 1000L)
                .expirationTimeSeconds(currentTime / 1000L + 3600L)
                .scope(getScopesString())
                .audience(serviceAccountCredentials.getTokenUri())
                .subject(serviceAccountCredentials.getAdminEmail())
                .build();

        String jwtHeaderJsonStr = gson.toJson(jwtHeader);
        String jwtClaimSetJsonStr = gson.toJson(jwtClaimSet);

        return org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString(jwtHeaderJsonStr.getBytes())
                + "."
                + org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString(jwtClaimSetJsonStr.getBytes());
    }

    /**
     * Build parameters for authorization request.
     *
     * @param jsonWebSignature - json web signature
     *
     * @return String
     */
    private String buildAuthorizationRequestParameters(String jsonWebSignature) {
        return String.format("grant_type=%s&assertion=%s", DEFAULT_GRANT_TYPE, jsonWebSignature);
    }

    /**
     * Sign content bytes with algorithm and private key.
     *
     * @param signatureAlgorithm    - sign algorithm
     * @param privateKey            - private key instance
     * @param contentBytes          - source bytes to sign
     *
     * @throws InvalidKeyException  -
     * @throws SignatureException   -
     *
     * @return byte[]
     */
    private static byte[] signWithAlgorithm(Signature signatureAlgorithm, PrivateKey privateKey, byte[] contentBytes)
            throws InvalidKeyException, SignatureException {
        signatureAlgorithm.initSign(privateKey);
        signatureAlgorithm.update(contentBytes);

        return signatureAlgorithm.sign();
    }

    /**
     * Get SHA256withRSA algorithm instance.
     *
     * @throws NoSuchAlgorithmException -
     *
     * @return
     */
    private static Signature getSha256WithRsaSignatureAlgorithm() throws NoSuchAlgorithmException {
        return Signature.getInstance("SHA256withRSA");
    }

    /**
     * Clear private key string from '-----BEGIN PRIVATE KEY-----' and '-----END PRIVATE KEY-----' and create {@link RSAPrivateKey}.
     *
     * @param privateKey - string private key
     *
     * @throws GeneralSecurityException -
     *
     * @return RSAPrivateKey
     */
    private static RSAPrivateKey createRsaPrivateKey(String privateKey) throws GeneralSecurityException {
        privateKey = privateKey.replaceAll("-----END PRIVATE KEY-----", "")
                .replaceAll("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll("\n", "");

        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKey);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        KeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);

        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }

    /**
     * Transform {@link #scopes} to string. Values delimited by ' '.
     *
     * @return String
     */
    private String getScopesString() {
        StringBuilder stringBuilder = new StringBuilder();

        for (String scope : scopes) {
            stringBuilder.append(scope).append(" ");
        }

        return stringBuilder.toString().trim();
    }

    /**
     * Convert string to UTF-8 bytes.
     *
     * @param sourceStr - source string
     *
     * @throws UnsupportedEncodingException -
     *
     * @return byte[]
     */
    private byte[] getUtf8Bytes(String sourceStr) throws UnsupportedEncodingException {
        return new String(sourceStr.getBytes(), "UTF-8").getBytes();
    }
}
