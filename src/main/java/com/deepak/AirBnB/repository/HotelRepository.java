package com.deepak.AirBnB.repository;

import com.deepak.AirBnB.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelRepository extends JpaRepository<Hotel, Long> {
}