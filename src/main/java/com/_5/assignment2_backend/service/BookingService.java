package com._5.assignment2_backend.service;

import com._5.assignment2_backend.model.Booking;
import com._5.assignment2_backend.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    public List<Booking> getAllBookings(PageRequest req) {
        List<Booking> bookings = bookingRepository.findAll(req.getSort());
        int start = (int) req.getOffset();
        int end = Math.min((start + req.getPageSize()), bookings.size());
        return bookings.subList(start,end);
    }

    public List<Booking> getAllBookingsByFilter(PageRequest req, String q) {
        List<Booking> bookings = bookingRepository.findAll(req.getSort());
        int start = (int) req.getOffset();
        int end = Math.min((start + req.getPageSize()), bookings.size());
        return bookings.subList(start,end);
    }


    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    public Booking createBooking(Booking booking) {
        return bookingRepository.save(booking);
    }

    public Optional<Booking> updateBooking(Long id, Booking updatedBooking) {
        return bookingRepository.findById(id).map(booking -> {
            booking.setId(updatedBooking.getId());
            booking.setEventid(updatedBooking.getEventid());
            booking.setUserid(updatedBooking.getUserid());
            return bookingRepository.save(booking);
        });
    }

    public boolean deleteBooking(Long id) {
        if (bookingRepository.existsById(id)) {
            bookingRepository.deleteById(id);
            return true;
        }
        return false;
    }
}

