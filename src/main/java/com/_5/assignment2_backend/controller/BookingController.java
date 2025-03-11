package com._5.assignment2_backend.controller;

import com._5.assignment2_backend.model.Booking;
import com._5.assignment2_backend.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings(
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
        List<Booking> bookings;

        if (q != null && !q.isEmpty()) {
            bookings = bookingService.getAllBookingsByFilter(pageRequest,q);
        } else {
            bookings = bookingService.getAllBookings(pageRequest);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Access-Control-Expose-Headers", "X-Total-Count");
        headers.add("X-Total-Count", String.valueOf(bookings.size()));
        return new ResponseEntity<>(bookings,headers,HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {
        return bookingService.getBookingById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Booking> createUser(@RequestBody Booking booking) {
        Booking newBooking = bookingService.createBooking(booking);
        return ResponseEntity.status(HttpStatus.CREATED).body(newBooking);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Booking> updateUser(@PathVariable Long id, @RequestBody Booking booking) {
        return bookingService.updateBooking(id, booking)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        if (bookingService.deleteBooking(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

