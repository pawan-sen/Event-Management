package com.party.attendees.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.party.attendees.dto.AttendeeReq;
import com.party.attendees.dto.AttendeesReq;
import com.party.attendees.dto.AttendeesStats;
import com.party.attendees.entities.Attendees;
import com.party.attendees.repository.AttendRepo;

@Service
public class AttendService {
	private static final Logger logger = LoggerFactory.getLogger(AttendService.class);

	AttendRepo attendRepo;

	AttendService(AttendRepo attendRepo) {
		this.attendRepo = attendRepo;
	}

	@Transactional
	public boolean addMultpleAttendee(String eventId, AttendeesReq attendeesReq) {
		boolean ret = true;
		try {
			logger.info("In addMultpleAttendee for " + eventId + " adding " + attendeesReq);
			List<AttendeeReq> attendsList = attendeesReq.getAttendeeReqs();

			for (AttendeeReq attends : attendsList) {
				Attendees attendeeToInsert = new Attendees(eventId, attends);
				logger.info("In addMultpleAttendee inserting " + attends);

				attendRepo.save(attendeeToInsert);
			}

		} catch (Exception e) {
			logger.error("Exception In addMultpleAttendee => ", e);
			ret = false;
		}

		return ret;

	}

	@Transactional
	public boolean addOneAttendee(String eventId, AttendeeReq attendeeReq) {
		boolean ret = true;
		try {
			Attendees attendeeToInsert = new Attendees(eventId, attendeeReq);
			logger.info("In addOneAttendee for " + eventId + " adding " + attendeeToInsert);
			attendRepo.save(attendeeToInsert);
		} catch (Exception e) {
			logger.error("Exception In addOneAttendee => ", e);
			ret = false;
		}

		return ret;
	}

	@Transactional
	public boolean deleteAttendeeForEvent(String eventId, String userId) {
		boolean ret = true;
		try {
			logger.info("In deleteAttendeeForEvent for " + eventId + " for user " + userId);
			int rows = attendRepo.deleteEntryByUserIdAndEventId(userId, eventId);

			logger.info("In deleteAttendeeForEvent rows deleted = " + rows);
			ret = rows > 0;
		} catch (Exception e) {
			logger.error("Exception In deleteAttendeeForEvent => ", e);
			ret = false;
		}
		return ret;
	}

	@Transactional
	public boolean deleteAllAttendForEvent(String eventId) {
		boolean ret = true;
		try {
			logger.info("In deleteAllAttendForEvent for " + eventId);
			attendRepo.deleteAllByEventId(eventId);
		} catch (Exception e) {
			logger.error("Exception In deleteAttendeeForEvent => ", e);
			ret = false;
		}

		return ret;
	}

	@Transactional
	public boolean updateRsvp(String eventId, String userId, String rsvp, String comment) {
		logger.info("In updateRsvp for " + eventId + " rsvp = " + rsvp + " comment = " + comment);

		int rows = attendRepo.updateRsvpStatus(userId, eventId, rsvp, comment);
		logger.info("In updateRsvp rows = " + rows);

		return rows > 0;
	}

	public List<String> getAllEventsNotAttended(String userId, List<String> events) {
		logger.info("In getAllEventsNotAttended for " + userId + " events => " + events);

		List<String> ret = attendRepo.getAllEventsNotAttending(userId, events);
		logger.info("In getAllEventsNotAttended user is not attending => " + ret);

		return ret;
	}

	public List<String> getAllEventsBeingAttendedByUser(String userId) {
		logger.info("In getAllEventsBeingAttendedByUser for " + userId);

		List<String> ret = attendRepo.getAllEventsBeingAttendedByUser(userId);
		logger.info("In getAllEventsBeingAttendedByUser user is attending => " + ret);

		return ret;
	}

	public List<Attendees> getAllAttendees(String eventId) {
		List<Attendees> allAttendees = attendRepo.getAllAttendeesByEventId(eventId);
		logger.info("In getAllAttendees all attendees for event = " + eventId + " => " + allAttendees);
		return allAttendees;
	}

	public AttendeesStats getEventAttendeesStats(String eventId) {
		AttendeesStats attendeesStats = new AttendeesStats();

		List<String> attending = attendRepo.getAttendeesWhoseRsvp(eventId, "Attending");
		List<String> notAttending = attendRepo.getAttendeesWhoseRsvp(eventId, "Not Attending");
		List<String> notSure = attendRepo.getAttendeesWhoseRsvp(eventId, "");

		attendeesStats.setAttending(attending);
		attendeesStats.setNotAttending(notAttending);
		attendeesStats.setNotSure(notSure);

		return attendeesStats;
	}
}
