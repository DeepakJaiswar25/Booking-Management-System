package com.deepak.AirBnB.repository;

import com.deepak.AirBnB.dto.HotelDto;
import com.deepak.AirBnB.entity.Hotel;
import com.deepak.AirBnB.entity.Inventory;
import com.deepak.AirBnB.entity.Room;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    void deleteByRoom(Room room);


    @Query("""
     Select distinct i.hotel from Inventory i
     where i.city =:city AND i.date between :startDate AND :endDate
      AND i.closed= false 
      AND (i.totalCount-i.reservedCount-i.bookedCount) >= :roomCount
   GROUP BY i.hotel,i.room
   having count(i.date)= :dateCount 
""")
    Page<Hotel> findHotelsWithAvailableInventory(
           @Param("city")  String city,
           @Param("startDate") LocalDate startDate,
           @Param("endDate") LocalDate endDate,
           @Param("roomCount") Integer roomCount,
           @Param("dateCount") long dateCount,
            Pageable pageable);

    @Query("""
    Select i from Inventory i
    where i.room.id=:roomId
    AND i.closed=false
    AND i.date between :checkInDate AND :checkOutDate
    AND (i.totalCount-i.bookedCount-i.reservedCount) >= :roomsCount
""")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Inventory> findAndLockAvailableInventory(
            @Param("roomId") Long roomId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate,
            @Param("roomsCount") Integer roomsCount);

    List<Inventory> findByHotelAndDateBetween(Hotel hotel, LocalDate startDate, LocalDate endDate);
}