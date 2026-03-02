package com.deepak.AirBnB.service.impl;

import com.deepak.AirBnB.entity.Inventory;
import com.deepak.AirBnB.entity.Room;
import com.deepak.AirBnB.repository.HotelRepository;
import com.deepak.AirBnB.repository.InventoryRepository;
import com.deepak.AirBnB.repository.RoomRepository;
import com.deepak.AirBnB.service.InventoryService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    @Override
    public void initializeRoomForAYear(Room room) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusYears(1);

        for(;!today.isAfter(endDate);today=today.plusDays(1)) {
            Inventory inventory = Inventory.builder()
                    .city(room.getHotel().getCity())
                    .hotel(room.getHotel())
                    .date(LocalDate.now())
                    .room(room)
                    .price(room.getBasePrice())
                    .bookedCount(0)
                    .totalCount(room.getTotalCount())
                    .surgeFactor(BigDecimal.ONE)
                    .closed(false)
                    .build();

            inventoryRepository.save(inventory);
        };

    }

    @Override
    public void deleteFutureInventories(Room room) {
        LocalDate today = LocalDate.now();
        inventoryRepository.deleteByDateAfterAndRoom(today, room);
    }
}
