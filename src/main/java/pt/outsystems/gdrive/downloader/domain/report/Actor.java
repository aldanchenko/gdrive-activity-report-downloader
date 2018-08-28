package pt.outsystems.gdrive.downloader.domain.report;

/**
 * Audit Activities Report Item Actor.
 */
public class Actor {

    private String callerType;
    private String email;
    private String profileId;
    private String key;

    public String getCallerType() {
        return callerType;
    }

    public void setCallerType(String callerType) {
        this.callerType = callerType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
