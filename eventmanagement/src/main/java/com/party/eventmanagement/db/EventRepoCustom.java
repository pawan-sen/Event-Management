package com.party.eventmanagement.db;

import java.time.LocalDate;
import java.util.List;

import com.party.eventmanagement.entity.EventDoc;

public interface EventRepoCustom {
    List<String> findAllPublicEventsIdWithFutureOrCurrentToDay(String userId, LocalDate curDate);
    
    List<EventDoc> findAllPastEventsCreatedBy(String userId, LocalDate curDate);
}