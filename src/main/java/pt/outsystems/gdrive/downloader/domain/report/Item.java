package pt.outsystems.gdrive.downloader.domain.report;

import java.util.List;

/**
 * Audit Activities Report item.
 */
public class Item {

    private String kind;
    private String ownerDomain;
    private String ipAddress;
    private ItemId id;
    private Actor actor;
    private List<Event> events;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getOwnerDomain() {
        return ownerDomain;
    }

    public void setOwnerDomain(String ownerDomain) {
        this.ownerDomain = ownerDomain;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public ItemId getId() {
        return id;
    }

    public void setId(ItemId id) {
        this.id = id;
    }

    public Actor getActor() {
        return actor;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}
