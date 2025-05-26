package com.example.demo.controller;

import com.example.demo.model.Booking;
import com.example.demo.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody Booking booking, @RequestParam Long roomId) {
        return bookingService.createBooking(booking, roomId);
    }

    @GetMapping("/by-guest")
    public ResponseEntity<List<Booking>> getBookingsByGuest(@RequestParam String guestName) {
        List<Booking> bookings = bookingService.getBookingsByGuest(guestName);
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }
}
