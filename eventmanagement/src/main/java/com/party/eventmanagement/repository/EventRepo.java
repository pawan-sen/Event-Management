package com.party.eventmanagement.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.party.eventmanagement.entity.EventDoc;

public interface EventRepo extends MongoRepository<EventDoc, String>, EventRepoCustom {

    // 1. Get all EventDoc by userId ordered by "fromDate" in descending order
    @Query(value = "{'userId': ?0, 'toDate': {$gte: ?1}}", sort = "{'fromDate': -1}")
    Optional<List<EventDoc>> findByUserIdOrderByFromDateDesc(String userId, LocalDate curDate);

    // 2. Private invites (future or current)
    @Query("{'isPrivateInvite': true, 'toDate': {$gte: ?0}}")
    List<EventDoc> findAllPrivateInvitesWithFutureOrCurrentToDay(LocalDate curDate);

    // 3. Public events (future or current)
    @Query("{'isPrivateInvite': false, 'toDate': {$gte: ?0}}")
    List<EventDoc> findAllPublicEventsWithFutureOrCurrentToDay(LocalDate curDate);

    // // 4. Past events created by user
    // @Query(value="{'userId': ?0, 'toDate': {$lt: ?1}}", sort = "{'fromDate':
    // -1}")
    // List<EventDoc> findAllPastEventsCreatedBy(String userId, LocalDate curDate);

    // 6. Running events
    @Query("{ 'eventId': { $in: ?0 }, 'toDate': { $gte: ?1 } }")
    List<EventDoc> findByRunningEventIdIn(List<String> eventIds, LocalDate curDate);

    // 7. Past events
    @Query("{ 'eventId': { $in: ?0 }, 'toDate': { $lt: ?1 } }")
    List<EventDoc> findByPastEventIdIn(List<String> eventIds, LocalDate curDate);
}