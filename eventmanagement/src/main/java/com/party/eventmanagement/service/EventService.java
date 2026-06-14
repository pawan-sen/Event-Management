package com.party.eventmanagement.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.party.eventmanagement.dto.EventMenu;
import com.party.eventmanagement.dto.EventReq;
import com.party.eventmanagement.entity.EventDoc;
import com.party.eventmanagement.external.AttendeesClient;
import com.party.eventmanagement.repository.EventRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class EventService {
	
	private static final Logger logger = LoggerFactory.getLogger(EventService.class);
	
	private final EventRepo repo;

	private final AttendeesClient attendeesClient;

	
	public List<EventDoc> getEvents() {
        return repo.findAll();
    }
    
    
    public EventDoc getEvent(String eventId) {
        return repo.findById(eventId).orElseThrow(() -> new RuntimeException("No Event found for event Id " + eventId));
    }
    
    public List<EventDoc> getUserEvents(String userId) {
        return repo.findByUserIdOrderByFromDateDesc(userId, LocalDate.now()).orElseThrow(() -> new RuntimeException("No Event found for User Id " + userId));
    }
    
    public List<EventMenu> getPublicEvents(String userId) {
    	logger.info("In getPublicEvents for userId = " + userId);
    	
    	// Public Events going on not created by this user
    	List<String> publicEvents = repo.findAllPublicEventsIdWithFutureOrCurrentToDay(userId, LocalDate.now());
    	logger.info("In getPublicEvents publicEvents not created by user are = " + publicEvents);
    	
    	
    	// Remove the events that user is attending
    	List<String> publicEventsNotAttending = attendeesClient.getAllEventsNotAttended(userId, publicEvents);
    	
    	logger.info("In getPublicEvents user is not attending = " + publicEventsNotAttending);
    	
    	if(publicEventsNotAttending == null || publicEventsNotAttending.isEmpty()) {
    		return new ArrayList<>();
    	}
    	
    	List<EventDoc> allCorrectEvents =  repo.findByRunningEventIdIn(publicEventsNotAttending, LocalDate.now());
    	
    	List<EventMenu> eventMenus = allCorrectEvents.stream().map(EventMenu::new).collect(Collectors.toList());
    	
    	return eventMenus;
    }
    
    public List<EventMenu> getAttendingEvents(String userId) {
    	logger.info("In getAttendingEvents for user => " + userId);
    	
    	// Remove the events that user is attending
    	List<String> allEventIdsBeingAttended = attendeesClient.getAllAttendingEvents(userId);
    	
    	logger.info("In getAttendingEvents allEventIdsBeingAttended by user => " + allEventIdsBeingAttended);
    	
    	if(allEventIdsBeingAttended == null || allEventIdsBeingAttended.isEmpty()) {
    		return new ArrayList<>();
    	}
    	
    	List<EventDoc> allCorrectEvents =  repo.findByRunningEventIdIn(allEventIdsBeingAttended, LocalDate.now());
    	
    	List<EventMenu> eventMenus = allCorrectEvents.stream().map(EventMenu::new).collect(Collectors.toList());
    	
    	return eventMenus;
    }
    
    public List<EventMenu> getPastEvents(String userId) {
    	List<EventDoc> pastEvents = repo.findAllPastEventsCreatedBy(userId, LocalDate.now());
    	
    	List<EventMenu> eventMenus = pastEvents.stream().map(EventMenu::new).collect(Collectors.toList());
    	
    	return eventMenus;
    }
    
    public List<EventMenu> getAttendedPastEvents(String userId) {
    	List<String> allEventIdsBeingAttended = attendeesClient.getAllAttendingEvents(userId);
    	
    	logger.info("In getAttendedPastEvents allEventIdsBeingAttended by user => " + allEventIdsBeingAttended);
    	
    	if(allEventIdsBeingAttended == null || allEventIdsBeingAttended.isEmpty()) {
    		return new ArrayList<>();
    	}
    	
    	List<EventDoc> pastEvents = repo.findByPastEventIdIn(allEventIdsBeingAttended, LocalDate.now());
    	
    	List<EventMenu> eventMenus = pastEvents.stream().map(EventMenu::new).collect(Collectors.toList());
    	
    	return eventMenus;
    }
    
    public boolean createEvent(EventReq req) {
    	boolean ret = true;
        try {
			EventDoc newEvent = new EventDoc(req);
			
			String isAttendeesAdded = "Success all attendees are registered";
			
			if(req.getAttendeesReq()!=null) {
				logger.info("In aattendeesReq => " + req.getAttendeesReq());
				isAttendeesAdded = attendeesClient.registerAttendees(newEvent.getEventId(), req.getAttendeesReq());
			}
			
			logger.info("createEvent response from registerAttendees => " + isAttendeesAdded);
			
        	if("Success all attendees are registered".equalsIgnoreCase(isAttendeesAdded)) {
        		try {
    				repo.insert(newEvent);
    				
    			} catch (Exception e) {
    				logger.error("Error in createEvent insert" + e.getStackTrace());
    				
    				if(req.getAttendeesReq()!=null) {
    					String result = attendeesClient.unregisterAttendees(newEvent.getEventId());
    					
    					logger.info("createEvent error delete attendees => " + result);
    				}
    				
    				ret = false;
    			}
        	}
			
		} 
        catch (Exception e) {
        	logger.error("Error in createEvent ", e);
        	ret = false;
		} 
        
        return ret;
    }
    
    public boolean updateEvent(EventDoc doc) {
    	try {
    		EventDoc cur = repo.findById(doc.getEventId()).orElseThrow(() -> new RuntimeException("No Event found"));
    		logger.info("updateEvent cur => " + cur.toString());
    		
    		cur.setEventName(doc.getEventName());
    		cur.setDescription(doc.getDescription());
    		cur.setLocation(doc.getLocation());
    		cur.setFromDate(doc.getFromDate());
    		cur.setToDate(doc.getToDate());
    		cur.setUserId(doc.getUserId());
    		cur.setUserName(doc.getUserName());
    		
			repo.save(cur);
		} 
        catch (Exception e) {
        	logger.error("Error in updateEvent => " + e.getStackTrace());
			return false;
		} 
        
        return true;
    }
    
    public boolean deleteEvent(String eventId) {
        repo.deleteById(eventId);
        
        return true;
    }
	
}
