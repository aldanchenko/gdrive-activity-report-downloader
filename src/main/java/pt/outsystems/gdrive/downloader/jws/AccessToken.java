package pt.outsystems.gdrive.downloader.jws;

import com.google.gson.annotations.SerializedName;

/**
 * TODO:
 */
public class AccessToken {

    @SerializedName("access_token")
    private String value;

    @SerializedName("token_type")
    private String tokenType;

    @SerializedName("expires_in")
    private Long expirationTimeSeconds;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Long getExpirationTimeSeconds() {
        return expirationTimeSeconds;
    }

    public void setExpirationTimeSeconds(Long expirationTimeSeconds) {
        this.expirationTimeSeconds = expirationTimeSeconds;
    }
}
