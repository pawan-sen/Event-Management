package com.party.eventmanagement.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventAIReq {
    private String eventFor;
    private Date eventFromDate;
    private Date eventToDate;
    private String eventLocation;
    private int numberOfGuests;
    private int budget;
}
