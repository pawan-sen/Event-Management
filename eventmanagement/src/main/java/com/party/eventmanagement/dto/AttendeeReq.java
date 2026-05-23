package com.party.eventmanagement.dto;

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
public class AttendeeReq {
	private String userId;
	private String userName;
	private String rsvpStatus;
	private String userComments;
}
