package com.party.attendees.entities;

import java.util.UUID;

import com.party.attendees.dto.AttendeeReq;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "EVENT_ATTENDEES_DETAILS")
public class Attendees {

	// @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private UUID id;

	@EmbeddedId
	private PrimaryKeyAttendees primaryId;

	@Column(name = "username")
	private String userName;
	@Column(name = "rsvpstatus")
	private String rsvpStatus;
	@Column(name = "usercomments")
	private String userComments;

	public Attendees(String eventId, AttendeeReq attend) {

		id = UUID.randomUUID();

		this.primaryId = new PrimaryKeyAttendees(eventId, attend.getUserId());

		this.setUserName(attend.getUserName());
		this.setUserComments(attend.getUserComments());
		this.setRsvpStatus(attend.getRsvpStatus());
	}

}
