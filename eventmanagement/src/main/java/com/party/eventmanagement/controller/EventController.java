package com.party.eventmanagement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.party.eventmanagement.entity.EventDoc;
import com.party.eventmanagement.entity.EventMenu;
import com.party.eventmanagement.entity.EventReq;
import com.party.eventmanagement.service.EventService;

@RestController
@RequestMapping("/events")
public class EventController {
    
	@Autowired
	private EventService service;
	
	// For test
    @GetMapping
    public ResponseEntity<List<EventDoc>> getEvents() {
        return ResponseEntity.ok(service.getEvents());
    }
    
    @GetMapping("/event/{eventId}")
    public ResponseEntity<EventDoc> getEvent(@PathVariable(name = "eventId") String eventId) {
        return ResponseEntity.ok(service.getEvent(eventId));
    }
    
    
    // 1. All public events available to attend but not attending - Discover new events
    @GetMapping("/publicEvent/{userId}")
    public ResponseEntity<List<EventMenu>> getPublicEventsNotAttending(@PathVariable(name = "userId") String userId) {
    	
    	try {
    		return ResponseEntity.ok(service.getPublicEvents(userId));
		} catch (Exception e) {
			
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
    }
    
    // 2. All events you are invited to(private) or you have registered. - Events you are attending
    @GetMapping("/getAttendingEvents/{userId}")
    public ResponseEntity<List<EventMenu>> getAttendingEvents(@PathVariable(name = "userId") String userId ) {
    	
    	try {
    		return ResponseEntity.ok(service.getAttendingEvents(userId));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
    	
        
    }
    
    // 3. All events created by this user that are running or will run - Events by you
    @GetMapping("/userCreatedEvents/{userId}")
    public ResponseEntity<List<EventDoc>> getUserEvents(@PathVariable String userId) {
        return ResponseEntity.ok(service.getUserEvents(userId));
    }
    
    // 4. All past events attended by user.
    @GetMapping("/getAttendedPastEvents/{userId}")
    public ResponseEntity<List<EventMenu>> getAttendedPastEvents(@PathVariable String userId) {
        return ResponseEntity.ok(service.getAttendedPastEvents(userId));
    }
    
    // 5. All past events created by a user
    @GetMapping("/pastEvent/{userId}")
    public ResponseEntity<List<EventMenu>> getPastEvents(String userId) {
    	
    	try {
    		return ResponseEntity.ok(service.getPastEvents(userId));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}

    }
    
    @PostMapping
    public ResponseEntity<String> createEvent(@RequestBody EventReq req) {
    	if(service.createEvent(req)) {
    		return ResponseEntity.ok("Event " + req.getEventName() + " created");
    	}
    	return ResponseEntity.internalServerError().body("Error in creating Event");
    }
    
    @PutMapping
    public ResponseEntity<String> updateEvent(@RequestBody EventDoc req) {
    	if(service.updateEvent(req)) {
    		return ResponseEntity.ok("Event updated");
    	}
        return ResponseEntity.internalServerError().body("Error in updating Event");
    }
    
    @DeleteMapping("/{eventId}")
    public ResponseEntity<String> deleteEvent(@PathVariable String eventId) {
    	if(service.deleteEvent(eventId)) {
    		return ResponseEntity.ok("Event " + eventId + " deleted");
    	}
        return ResponseEntity.internalServerError().body("Error in deleting Event " + eventId);
    }
}
