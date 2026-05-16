package com.deepak.AirBnB.repository;

import com.deepak.AirBnB.entity.Hotel;
import com.deepak.AirBnB.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HotelRepository extends JpaRepository<Hotel, Long> {
    List<Hotel> findByOwner(User user);
}