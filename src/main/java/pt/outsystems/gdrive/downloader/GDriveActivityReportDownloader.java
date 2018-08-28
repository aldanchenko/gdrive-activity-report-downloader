package pt.outsystems.gdrive.downloader;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
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
 * FIXME: under construction.
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
     * Contains requested scopes for Reports API.
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
        String signature = buildSignature(serviceAccountCredentials);

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
     * Build signature for Json web signature.
     *
     * @param serviceAccountCredentials - service account credentials
     * @throws UnsupportedEncodingException -
     * @throws GeneralSecurityException     -
     *
     * @return String
     */
    private String buildSignature(ServiceAccountCredentials serviceAccountCredentials)
                            throws UnsupportedEncodingException, GeneralSecurityException {
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
                .build();

        String jwtHeaderJsonStr = gson.toJson(jwtHeader);
        String jwtClaimSetJsonStr = gson.toJson(jwtClaimSet);

        String jwtContentStr = org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString(jwtHeaderJsonStr.getBytes())
                + "."
                + com.google.api.client.util.Base64.encodeBase64URLSafeString(jwtClaimSetJsonStr.getBytes());

        byte[] contentBytes = getUtf8Bytes(jwtContentStr);

        byte[] jsonWebSignature = signWithAlgorithm(getSha256WithRsaSignatureAlgorithm(),
                                        createRsaPrivateKey(serviceAccountCredentials.getPrivateKey()), contentBytes);

        return org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString(jsonWebSignature);
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
                .build();

        String jwtHeaderJsonStr = gson.toJson(jwtHeader);
        String jwtClaimSetJsonStr = gson.toJson(jwtClaimSet);

        return org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString(jwtHeaderJsonStr.getBytes())
                + "."
                + com.google.api.client.util.Base64.encodeBase64URLSafeString(jwtClaimSetJsonStr.getBytes());
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
