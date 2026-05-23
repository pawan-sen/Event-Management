package com.party.eventmanagement.dto;

import java.time.LocalDate;

import com.party.eventmanagement.entity.EventDoc;

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
public class EventMenu {
	String eventId;
	String eventName;
	String description;
	String location;
	LocalDate fromDate;
	boolean isOnGoing;
	
	public EventMenu(EventDoc doc) {
		this.eventId = doc.getEventId();
		this.eventName = doc.getEventName();
		this.description = doc.getDescription();
		this.location = doc.getLocation();
		this.fromDate = doc.getFromDate();
		
		LocalDate today = LocalDate.now();
		isOnGoing = today.isAfter(doc.getFromDate()) || today.equals(doc.getFromDate());
	}
}
