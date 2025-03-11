package com._5.assignment2_backend.service;

import com._5.assignment2_backend.model.Event;
import com._5.assignment2_backend.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    public List<Event> getAllEvents(PageRequest req) {
        List<Event> events = eventRepository.findAll(req.getSort());
        int start = (int) req.getOffset();
        int end = Math.min((start + req.getPageSize()), events.size());
        return events.subList(start,end);
    }

    public List<Event> getAllEventsByFilter(PageRequest req, String q) {
        List<Event> events = eventRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(q,q, req.getSort());
        int start = (int) req.getOffset();
        int end = Math.min((start + req.getPageSize()), events.size());
        return events.subList(start,end);
    }


    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    public Optional<Event> updateEvent(Long id, Event updatedEvent) {
        return eventRepository.findById(id).map(event -> {
            event.setTitle(updatedEvent.getTitle());
            event.setViews(updatedEvent.getViews());
            event.setId(updatedEvent.getId());
            event.setDescription(updatedEvent.getDescription());
            event.setTimeend(updatedEvent.getTimeend());
            event.setTimestart(updatedEvent.getTimestart());
            return eventRepository.save(event);
        });
    }

    public boolean deleteEvent(Long id) {
        if (eventRepository.existsById(id)) {
            eventRepository.deleteById(id);
            return true;
        }
        return false;
    }
}

