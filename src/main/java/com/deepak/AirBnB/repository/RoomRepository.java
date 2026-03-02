package com.deepak.AirBnB.repository;

import com.deepak.AirBnB.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
}