package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.model.Hotel;

public interface HotelRepository extends JpaRepository<Hotel, Long> {
}
