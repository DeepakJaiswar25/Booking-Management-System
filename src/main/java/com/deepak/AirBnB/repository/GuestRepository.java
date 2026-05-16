package com.deepak.AirBnB.repository;

import com.deepak.AirBnB.entity.Guest;
import com.deepak.AirBnB.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GuestRepository extends JpaRepository<Guest, Long> {
    List<Guest> findByUser(User user);
}