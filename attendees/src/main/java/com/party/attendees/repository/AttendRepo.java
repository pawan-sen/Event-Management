package com.party.attendees.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.party.attendees.entities.Attendees;
import com.party.attendees.entities.PrimaryKeyAttendees;

public interface AttendRepo extends JpaRepository<Attendees, PrimaryKeyAttendees> {
	
	// Update RSVP status with a new string for a given userId and eventId
    @Modifying
    @Query("UPDATE Attendees a SET a.rsvpStatus = :newRsvp, a.userComments = :comment WHERE a.primaryId.userId = :userId AND a.primaryId.eventId = :eventId")
    int updateRsvpStatus(String userId, String eventId, String newRsvp, String comment);

    // Get all attendees for a specific eventId
	@Transactional(readOnly = true)
    @Query("SELECT a FROM Attendees a WHERE a.primaryId.eventId = :eventId")
    List<Attendees> getAllAttendeesByEventId(String eventId);

    // Delete an entry for a specific userId and eventId
    @Modifying
    @Query("DELETE FROM Attendees a WHERE a.primaryId.userId = :userId AND a.primaryId.eventId = :eventId")
    int deleteEntryByUserIdAndEventId(String userId, String eventId);

    // Delete all entries for a specific eventId
    @Modifying
    @Query("DELETE FROM Attendees a WHERE a.primaryId.eventId = :eventId")
    int deleteAllByEventId(String eventId);
	
    @Transactional(readOnly = true)
    @Query(value = """
    SELECT eventid 
    FROM event_attendees_details 
    WHERE eventid IN (:events)
    AND eventid NOT IN (
        SELECT eventid FROM event_attendees_details WHERE userid = :userId
    )
    """, nativeQuery = true)
    List<String> getAllEventsNotAttending(String userId, List<String> events);
    
    @Transactional(readOnly = true)
    @Query("SELECT a.primaryId.eventId FROM Attendees a WHERE a.primaryId.userId = :userId")
    List<String> getAllEventsBeingAttendedByUser(String userId);
    
    @Transactional(readOnly = true)
    @Query("SELECT a.primaryId.userId FROM Attendees a WHERE a.primaryId.eventId = :eventId and a.rsvpStatus = :rsvp")
    List<String> getAttendeesWhoseRsvp(String eventId, String rsvp);
}
