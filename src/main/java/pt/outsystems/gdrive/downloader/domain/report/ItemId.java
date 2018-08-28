package pt.outsystems.gdrive.downloader.domain.report;

/**
 * Audit Activities Report Item id.
 */
public class ItemId {

    private String time;
    private String uniqQualifier;
    private String applicationName;
    private String customerId;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUniqQualifier() {
        return uniqQualifier;
    }

    public void setUniqQualifier(String uniqQualifier) {
        this.uniqQualifier = uniqQualifier;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
}
