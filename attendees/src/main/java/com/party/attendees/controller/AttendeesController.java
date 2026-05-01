package com.party.attendees.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.party.attendees.entities.AttendeeReq;
import com.party.attendees.entities.Attendees;
import com.party.attendees.entities.AttendeesReq;
import com.party.attendees.service.AttendService;

@RestController
@RequestMapping("/attendees")
public class AttendeesController {

	private AttendService attendService;

	AttendeesController(AttendService attendService) {
		this.attendService = attendService;
	}

	@GetMapping("/test")
	public ResponseEntity<String> test() {
		return ResponseEntity.ok("test");
	}

	@PostMapping("/getAllEventsNotAttended/{userId}")
	public ResponseEntity<?> getAllEventsNotAttended(@PathVariable(name = "userId") String userId,
			@RequestBody List<String> eventsList) {
		return ResponseEntity.ok(attendService.getAllEventsNotAttended(userId, eventsList));
	}

	@PostMapping("/getAllAttendingEvents/{userId}")
	public ResponseEntity<?> getAllAttendingEvents(@PathVariable(name = "userId") String userId) {
		return ResponseEntity.ok(attendService.getAllEventsBeingAttendedByUser(userId));
	}

	@PostMapping("/registerAttendees/{eventId}")
	public ResponseEntity<String> registerAttendees(@PathVariable(name = "eventId") String eventId,
			@RequestBody AttendeesReq attendeesReq) {
		if (attendService.addMultpleAttendee(eventId, attendeesReq)) {
			return ResponseEntity.ok("Success all attendees are registered");
		}

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error registering all attendees");
	}

	@PostMapping("/registerForEvent/{eventId}")
	public ResponseEntity<String> registerForEvent(@PathVariable(name = "eventId") String eventId,
			@RequestBody AttendeeReq attendeeReq) {
		if (attendService.addOneAttendee(eventId, attendeeReq)) {
			return ResponseEntity.ok("Success in the registering " + attendeeReq.getUserId());
		}

		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body("Error registering the attendee " + attendeeReq.getUserId());
	}

	@PatchMapping("/rsvp/{eventId}")
	public ResponseEntity<String> updateRSVP(@PathVariable(name = "eventId") String eventId,
			@RequestParam(required = true) Map<String, String> reqParam) {
		String userId = reqParam.getOrDefault("userId", "");
		String rsvp = reqParam.getOrDefault("rsvp", "");
		String comment = reqParam.getOrDefault("comment", "");

		if (attendService.updateRsvp(eventId, userId, rsvp, comment)) {
			return ResponseEntity.ok("updated");
		}

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error in updating event " + eventId);
	}

	@GetMapping("/{eventId}")
	public ResponseEntity<List<Attendees>> getAttendees(@PathVariable(name = "eventId") String eventId) {
		return ResponseEntity.ok(attendService.getAllAttendees(eventId));
	}

	// Get all attendees that are attending vs not attending vs no rsvp till now for
	// private events
	public ResponseEntity<?> getEventStats(@PathVariable(name = "eventId") String eventId) {
		try {
			return ResponseEntity.ok(attendService.getEventAttendeesStats(eventId));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error getting details for event " + eventId);
		}
	}

	@DeleteMapping("/{eventId}/{userId}")
	public ResponseEntity<String> deleteAttendee(@PathVariable(name = "eventId") String eventId,
			@PathVariable(name = "userId") String userId) {

		if (attendService.deleteAttendeeForEvent(eventId, userId)) {
			return ResponseEntity.status(HttpStatus.OK).body(userId + " deleted for event " + eventId);
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body("Error in deleting " + userId + " deleted for event " + eventId);
	}

	@DeleteMapping("/event/{eventId}")
	public ResponseEntity<String> deleteAllAttendeeForEvent(@PathVariable(name = "eventId") String eventId) {
		if (attendService.deleteAllAttendForEvent(eventId)) {
			return ResponseEntity.status(HttpStatus.OK).body("Deleted all attendees for event " + eventId);
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error in deleting event " + eventId);
	}

}
