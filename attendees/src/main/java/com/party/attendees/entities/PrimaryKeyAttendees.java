package com.party.attendees.entities;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class PrimaryKeyAttendees {
	
	@Column(name = "eventid")
	private String eventId;
	@Column(name = "userid")
    private String userId;
    
    public PrimaryKeyAttendees() {
    	
    }

    public PrimaryKeyAttendees(String eventId, String userId) {
        this.eventId = eventId;
        this.userId = userId;
    }
    
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrimaryKeyAttendees that = (PrimaryKeyAttendees) o;
        return Objects.equals(eventId, that.eventId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, userId);
    }
}
