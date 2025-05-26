package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.model.Booking;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByGuestName(String guestName);

    @Query("SELECT b FROM Booking b WHERE b.room.id = :roomId AND " +
            "(:toDate >= b.fromDate AND :fromDate <= b.toDate)")
    List<Booking> findByRoomIdAndDateRange(
            @Param("roomId") Long roomId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );
}
