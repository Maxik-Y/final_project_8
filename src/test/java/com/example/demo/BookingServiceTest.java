package com.example.demo;
import com.example.demo.model.Booking;
import com.example.demo.model.Room;
import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.RoomRepository;
import com.example.demo.service.BookingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private BookingService bookingService;

    @Test
    public void testCreateBooking_Success() {
        Room room = new Room();
        room.setId(1L);
        Booking booking = new Booking();
        booking.setGuestName("John Doe");
        booking.setFromDate(LocalDate.now());
        booking.setToDate(LocalDate.now().plusDays(1));

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(bookingRepository.findByRoomIdAndDateRange(1L, booking.getFromDate(), booking.getToDate()))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        ResponseEntity<?> response = bookingService.createBooking(booking, 1L);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    public void testCreateBooking_RoomNotFound() {
        when(roomRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = bookingService.createBooking(new Booking(), 1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        assertTrue(
                response.getBody().toString().contains("Room not found with id: 1"),
                "Повідомлення про помилку має містити ID кімнати"
        );
    }

    @Test
    public void testCreateBooking_RoomAlreadyBooked() {
        Room room = new Room();
        room.setId(1L);
        Booking existingBooking = new Booking();

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(bookingRepository.findByRoomIdAndDateRange(any(), any(), any()))
                .thenReturn(List.of(existingBooking));

        Booking newBooking = new Booking();
        newBooking.setFromDate(LocalDate.now());
        newBooking.setToDate(LocalDate.now().plusDays(1));

        ResponseEntity<?> response = bookingService.createBooking(newBooking, 1L);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(response.getBody(), "Room already booked");
    }

    @Test
    public void testGetBookingsByGuest() {
        Booking booking = new Booking();
        booking.setGuestName("John Doe");

        when(bookingRepository.findByGuestName("John Doe")).thenReturn(List.of(booking));

        List<Booking> bookings = bookingService.getBookingsByGuest("John Doe");

        assertEquals(1, bookings.size());
        assertEquals("John Doe", bookings.get(0).getGuestName());
    }
}

