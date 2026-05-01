package com.party.eventmanagement.service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.party.eventmanagement.db.EventRepo;
import com.party.eventmanagement.entity.EventDoc;
import com.party.eventmanagement.entity.EventMenu;
import com.party.eventmanagement.entity.EventReq;

import lombok.RequiredArgsConstructor;
import reactor.util.retry.Retry;

@Service
@RequiredArgsConstructor
@Transactional
public class EventService {
	
	private static final Logger logger = LoggerFactory.getLogger(EventService.class);
	
	private final EventRepo repo;
	
	private final WebClient.Builder webClientBuilder;

	
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
    	List<String> publicEventsNotAttending = webClientBuilder.build()
				.post()
				.uri("http://attendees/attendees/getAllEventsNotAttended/{userId}", userId)
				.bodyValue(publicEvents)
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<List<String>>() {})
				.timeout(Duration.ofSeconds(5))
				.block();
    	
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
    	List<String> allEventIdsBeingAttended = webClientBuilder.build()
				.post()
				.uri("http://attendees/attendees/getAllAttendingEvents/{userId}", userId)
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<List<String>>() {})
				.timeout(Duration.ofSeconds(5))
				.block();
    	
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
    	List<String> allEventIdsBeingAttended = webClientBuilder.build()
				.post()
				.uri("http://attendees/attendees/getAllAttendingEvents/{userId}", userId)
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<List<String>>() {})
				.timeout(Duration.ofSeconds(5))
				.block();
    	
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
        	
//        	webClientBuilder
        	
			EventDoc newEvent = new EventDoc(req);
			
			String isAttendeesAdded = "Success all attendees are registered";
			
			if(req.getAttendeesReq()!=null) {
				System.out.println("In aattendeesReq => " + req.getAttendeesReq());
				isAttendeesAdded = webClientBuilder.build()
						.post()
						.uri("http://attendees/attendees/registerAttendees/{eventId}", newEvent.getEventId())
						.bodyValue(req.getAttendeesReq())
						.retrieve()
						.bodyToMono(String.class)
						.timeout(Duration.ofSeconds(15))
						.block();
			}
			
			logger.info("createEvent response from registerAttendees => " + isAttendeesAdded);

			System.out.println("createEvent response from registerAttendees => " + isAttendeesAdded);
			
        	if("Success all attendees are registered".equalsIgnoreCase(isAttendeesAdded)) {
        		try {
    				repo.insert(newEvent);
    				
    			} catch (Exception e) {
    				logger.error("Error in createEvent insert" + e.getStackTrace());
    				
    				if(req.getAttendeesReq()!=null) {
    					String result = webClientBuilder.build()
							.delete()
							.uri("http://attendees/attendees/event/{eventId}", newEvent.getEventId())
							.retrieve()
							.bodyToMono(String.class)
							.timeout(Duration.ofSeconds(5))
							.retryWhen(Retry.fixedDelay(5, Duration.ofSeconds(2)))
							.block();
    					
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
