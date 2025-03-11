package com._5.assignment2_backend.controller;

import com._5.assignment2_backend.model.Event;
import com._5.assignment2_backend.model.User;
import com._5.assignment2_backend.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents(
            @RequestParam(defaultValue = "0") int _start,
            @RequestParam(defaultValue = "10") int _end,
            @RequestParam(defaultValue = "id") String _sort,
            @RequestParam(defaultValue = "ASC") String _order,
            @RequestParam(defaultValue = "") String q) {
        int page = _start / _end;
        int size = _end;

        // Handle sorting order
        Sort sort = _order.equalsIgnoreCase("ASC") ? Sort.by(Sort.Order.asc(_sort)) : Sort.by(Sort.Order.desc(_sort));
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        List<Event> events;

        if (q != null && !q.isEmpty()) {
            events = eventService.getAllEventsByFilter(pageRequest,q);
        } else {
            events = eventService.getAllEvents(pageRequest);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Access-Control-Expose-Headers", "X-Total-Count");
        headers.add("X-Total-Count", String.valueOf(events.size()));
        return new ResponseEntity<>(events,headers,HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        return eventService.getEventById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Event> createUser(@RequestBody Event event) {
        Event newEvent = eventService.createEvent(event);
        return ResponseEntity.status(HttpStatus.CREATED).body(newEvent);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Event> updateUser(@PathVariable Long id, @RequestBody Event event) {
        return eventService.updateEvent(id, event)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        if (eventService.deleteEvent(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

