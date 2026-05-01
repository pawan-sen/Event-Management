package com.party.eventmanagement.entity;

import java.time.LocalDate;

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
		this.eventId = doc.eventId;
		this.eventName = doc.eventName;
		this.description = doc.description;
		this.location = doc.location;
		this.fromDate = doc.fromDate;
		
		LocalDate today = LocalDate.now();
		isOnGoing = today.isAfter(doc.fromDate) || today.equals(doc.fromDate);
	}
}
