package com.example.demo.service;

import com.example.demo.model.Booking;
import com.example.demo.model.Room;
import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

    public ResponseEntity<?> createBooking(Booking booking, Long roomId) {
        logger.info("Attempt to create booking for roomId: {}", roomId);

        try {
            Room room = roomRepository.findById(roomId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Room not found with id: " + roomId
                    ));

            boolean isRoomAvailable = bookingRepository
                    .findByRoomIdAndDateRange(roomId, booking.getFromDate(), booking.getToDate())
                    .isEmpty();

            if (!isRoomAvailable) {
                logger.warn("Room {} already booked for dates {} - {}", roomId, booking.getFromDate(), booking.getToDate());
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Room already booked");
            }

            booking.setRoom(room);
            Booking savedBooking = bookingRepository.save(booking);
            logger.info("Booking created successfully: {}", savedBooking.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedBooking);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        }
    }

    public List<Booking> getBookingsByGuest(String guestName) {
        return bookingRepository.findByGuestName(guestName);
    }
}
