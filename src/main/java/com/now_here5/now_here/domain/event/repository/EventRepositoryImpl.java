package com.now_here5.now_here.domain.event.repository;


import com.now_here5.now_here.domain.event.entity.Event;
import com.now_here5.now_here.domain.event.entity.Location;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class EventRepositoryImpl implements EventRepository {
    private final EntityManager em;

    @Override
    public List<Event> getEventList(boolean status) {
        try{
            return em.createQuery("select e from Event e where e.status = :status", Event.class)
                    .setParameter("status", status)
                    .getResultList();
        }catch (Exception e){
            log.error("getEventList error", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Event getEventDetail(Long eventId) {
        try{
            return em.find(Event.class, eventId);
        }catch (Exception e){
            log.error("getEventDetail error", e);
            throw new RuntimeException(e.getMessage());
        }
    }


    @Override
    public List<Event> getSignedEventsByMember(boolean active, Long memberId) {
        try{
            return em.createQuery("select e from Member m " +
                            "join m.event e " +
                            "where m.id = :memberId and e.status = :active", Event.class)
                    .setParameter("memberId", memberId)
                    .setParameter("active", active)
                    .getResultList();

        }catch(Exception e){
            log.error("getSignedEventsByMember error = {} ", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }


    @Override
    public void updateEventStatusById(Long eventId, boolean status) {
        try{
            em.createQuery("update Event e set e.status = :status where e.id = :eventId")
                    .setParameter("status", status)
                    .setParameter("eventId", eventId)
                    .executeUpdate();
        }catch(Exception e){
            log.error("updateEventStatus error = {} ", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void createEvent(Event event) {
        try{
            em.persist(event);
        }catch(Exception e){
            log.error("createEvent error = {} ", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void createLocation(Location location) {
        try{
            em.persist(location);
        }catch(Exception e){
            log.error("createLocation error = {} ", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Location getLocationById(Long locationId) {
        try{
            return em.find(Location.class, locationId);
        } catch (Exception e) {
            log.error("failed to find location by id = {}", locationId);
            return null;
        }
    }

    @Override
    public List<Location> getLocationList() {
        try{
            return em.createQuery("select l from Location l", Location.class)
                    .getResultList();
        }catch(Exception e){
            log.error("getLocationList error = {} ", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void deleteLocationById(Long locationId) {
        try{
            em.createQuery("delete from Location l where l.id = :locationId")
                    .setParameter("locationId", locationId)
                    .executeUpdate();
        }catch(Exception e){
            log.error("deleteLocationById error = {} ", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

}
