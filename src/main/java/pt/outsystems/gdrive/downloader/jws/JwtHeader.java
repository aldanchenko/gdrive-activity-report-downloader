package pt.outsystems.gdrive.downloader.jws;

import com.google.gson.annotations.SerializedName;

public class JwtHeader {

    @SerializedName("alg")
    private String algorithm;

    @SerializedName("typ")
    private String type;

    @SerializedName("kid")
    private String keyId;

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    /**
     * Builder for {@link JwtHeader}.
     */
    public static class Builder {
        private String algorithm;
        private String type;
        private String keyId;

        public JwtHeader build() {
            JwtHeader jwtHeader = new JwtHeader();

            jwtHeader.setAlgorithm(this.algorithm);
            jwtHeader.setType(this.type);
            jwtHeader.setKeyId(this.keyId);

            return jwtHeader;
        }

        public Builder algorithm(String algorithm) {
            this.algorithm = algorithm;

            return this;
        }

        public Builder type(String type) {
            this.type = type;

            return this;
        }

        public Builder keyId(String keyId) {
            this.keyId = keyId;

            return this;
        }
    }
}
