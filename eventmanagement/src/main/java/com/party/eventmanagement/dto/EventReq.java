package com.party.eventmanagement.dto;

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
public class EventReq {
	String eventName;
	String description;
	String location;
	LocalDate fromDate;
	LocalDate toDate;
	String userId;
	String userName;
	boolean isPrivateInvite;
	
	AttendeesReq attendeesReq;
}
