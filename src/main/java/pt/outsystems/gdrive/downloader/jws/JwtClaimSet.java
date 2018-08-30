package pt.outsystems.gdrive.downloader.jws;

import com.google.gson.annotations.SerializedName;

public class JwtClaimSet {

    @SerializedName("exp")
    private Long expirationTimeSeconds;

    @SerializedName("iat")
    private Long issuedAtTimeSeconds;

    @SerializedName("iss")
    private String issuer;

    @SerializedName("aud")
    private String audience;

    @SerializedName("scope")
    private String scope;

    @SerializedName("sub")
    private String subject;

    /**
     * Builder for {@link JwtClaimSet}.
     */
    public static class Builder {
        private Long expirationTimeSeconds;
        private Long issuedAtTimeSeconds;
        private String issuer;
        private String audience;
        private String scope;
        private String subject;

        public JwtClaimSet build() {
            JwtClaimSet jwtClaimSet = new JwtClaimSet();

            jwtClaimSet.setAudience(this.audience);
            jwtClaimSet.setSubject(this.subject);
            jwtClaimSet.setExpirationTimeSeconds(this.expirationTimeSeconds);
            jwtClaimSet.setIssuedAtTimeSeconds(this.issuedAtTimeSeconds);
            jwtClaimSet.setIssuer(this.issuer);
            jwtClaimSet.setScope(this.scope);

            return jwtClaimSet;
        }

        public Builder expirationTimeSeconds(Long expirationTimeSeconds) {
            this.expirationTimeSeconds = expirationTimeSeconds;

            return this;
        }

        public Builder issuedAtTimeSeconds(Long issuedAtTimeSeconds) {
            this.issuedAtTimeSeconds = issuedAtTimeSeconds;

            return this;
        }

        public Builder issuer(String issuer) {
            this.issuer = issuer;

            return this;
        }

        public Builder audience(String audience) {
            this.audience = audience;

            return this;
        }

        public Builder scope(String scope) {
            this.scope = scope;

            return this;
        }

        public Builder subject(String delegatedAccessEmail) {
            this.subject = delegatedAccessEmail;

            return this;
        }
    }

    public Long getExpirationTimeSeconds() {
        return expirationTimeSeconds;
    }

    public void setExpirationTimeSeconds(Long expirationTimeSeconds) {
        this.expirationTimeSeconds = expirationTimeSeconds;
    }

    public Long getIssuedAtTimeSeconds() {
        return issuedAtTimeSeconds;
    }

    public void setIssuedAtTimeSeconds(Long issuedAtTimeSeconds) {
        this.issuedAtTimeSeconds = issuedAtTimeSeconds;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
