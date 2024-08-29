package com.now_here5.now_here.domain.event.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;


@Slf4j
@Repository
public class EventSchedulerRepository {
    private static final ConcurrentHashMap<Long, ScheduledFuture<?>> eventScheduler = new ConcurrentHashMap<>();

    public void addEventScheduler(Long eventId, ScheduledFuture<?> scheduledFuture) {
        try{
            eventScheduler.put(eventId, scheduledFuture);
        }catch (Exception e){
            log.error("Failed to add event scheduler: {}", e.getMessage());
            throw new RuntimeException("Failed to add event scheduler");
        }

    }

    public  void removeEventScheduler(Long eventId) {
        try{
            ScheduledFuture<?> scheduledFuture = getEventScheduler(eventId);
            if (scheduledFuture != null && !scheduledFuture.isDone()) {
                scheduledFuture.cancel(false);  // false = 이미 실행 중인 작업은 중단하지 않음
            }
            eventScheduler.remove(eventId);
        }catch (Exception e){
            log.error("Failed to remove event scheduler: {}", e.getMessage());
            throw new RuntimeException("Failed to remove event scheduler");
        }

    }

    public  ScheduledFuture<?> getEventScheduler(Long eventId) {
        try{
            return eventScheduler.get(eventId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Long> getEventSchedulerKeys() {
        try{
            return List.copyOf(eventScheduler.keySet());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
