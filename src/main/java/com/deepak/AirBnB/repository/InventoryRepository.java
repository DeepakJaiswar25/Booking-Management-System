package com.deepak.AirBnB.repository;

import com.deepak.AirBnB.entity.Inventory;
import com.deepak.AirBnB.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    void deleteByDateAfterAndRoom(LocalDate today, Room room);
}