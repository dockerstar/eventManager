package dev.sorokin.eventnotificator.entity;

import dev.sorokin.eventcommon.kafka.ChangesField;
import jdk.dynalink.linker.LinkerServices;

import java.util.List;

public class Payload {
    private Long eventId;
    private Long changedById;
    private List<ChangesField> changes;

    public Payload(Long eventId, Long changedById, List<ChangesField> changes) {
        this.eventId = eventId;
        this.changedById = changedById;
        this.changes = changes;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getChangedById() {
        return changedById;
    }

    public void setChangedById(Long changedById) {
        this.changedById = changedById;
    }

    public List<ChangesField> getChanges() {
        return changes;
    }

    public void setChanges(List<ChangesField> changes) {
        this.changes = changes;
    }

    @Override
    public String toString() {
        return "Payload{" +
                "eventId=" + eventId +
                ", changedById=" + changedById +
                ", changes=" + changes +
                '}';
    }
}
