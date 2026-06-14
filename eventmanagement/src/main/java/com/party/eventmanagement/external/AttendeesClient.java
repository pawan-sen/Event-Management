package com.party.eventmanagement.external;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.party.eventmanagement.dto.AttendeesReq;

@FeignClient(name = "attendees")
public interface AttendeesClient {
    
    @PostMapping("/attendees/getAllEventsNotAttended/{userId}")
    List<String> getAllEventsNotAttended(@PathVariable(name = "userId") String userId,
    									@RequestBody(required = true) List<String> publicEvents);

    @PostMapping("/attendees/getAllAttendingEvents/{userId}")
    List<String> getAllAttendingEvents(@PathVariable(name = "userId") String userId);

    @PostMapping("/attendees/registerAttendees/{eventId}")
    String registerAttendees(@PathVariable(name = "eventId") String eventId,
    						@RequestBody(required = true) AttendeesReq attendeesReq);

    @DeleteMapping("/attendees/event/{eventId}")
    String unregisterAttendees(@PathVariable(name = "eventId") String eventId);
}
