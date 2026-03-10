package com.deepak.AirBnB.repository;

import com.deepak.AirBnB.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}