package com.party.eventmanagement.db;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.party.eventmanagement.entity.EventDoc;

@Repository
public class EventRepoImpl implements EventRepoCustom {
	@Autowired
    private MongoTemplate mongoTemplate;

	@Override
    public List<String> findAllPublicEventsIdWithFutureOrCurrentToDay(String userId, LocalDate curDate) {
        Query query = new Query();
        query.addCriteria(Criteria.where("isPrivateInvite").is(false)
                                  .and("toDate").gte(curDate)
                                  .and("userId").ne(userId));
        query.fields().include("eventId");
        List<EventDoc> docs = mongoTemplate.find(query, EventDoc.class, "Event-Detail");
        return docs.stream()
                   .map(d -> d.getEventId())  // extract just the _id
                   .collect(Collectors.toList());
    }
	
	@Override
	public List<EventDoc> findAllPastEventsCreatedBy(String userId, LocalDate curDate) {
		Sort sort = Sort.by(Direction.DESC, "fromDate");
		
        Query query = new Query();
        query.addCriteria(Criteria.where("isPrivateInvite").is(false)
        						  .and("userid").is(userId)
                                  .and("toDate").lt(curDate));
        
        query.with(sort);
        
        return mongoTemplate.find(query, EventDoc.class, "Event-Detail");

    }
}
