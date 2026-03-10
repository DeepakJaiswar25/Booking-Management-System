package com.deepak.AirBnB.repository;

import com.deepak.AirBnB.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestRepository extends JpaRepository<Guest, Long> {
}