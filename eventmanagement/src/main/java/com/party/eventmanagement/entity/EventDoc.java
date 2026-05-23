package com.party.eventmanagement.entity;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.party.eventmanagement.dto.EventReq;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Document("Event-Detail")
public class EventDoc {
	
	@Id
	String eventId;
	String eventName;
	String description;
	String location;
	LocalDate fromDate;
	LocalDate toDate;
	String userId;
	String userName;
	boolean isPrivateInvite;
	
	public EventDoc(EventReq req) {
		this.eventId = UUID.randomUUID().toString();
		
		this.eventName = req.getEventName();
		this.description = req.getDescription();
		this.location = req.getLocation();
		this.fromDate = req.getFromDate();
		this.toDate = req.getToDate();
		this.userId = req.getUserId();
		this.userName = req.getUserName();
		this.isPrivateInvite = req.isPrivateInvite();
	}
}
