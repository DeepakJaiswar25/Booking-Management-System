package com.deepak.AirBnB.service.impl;

import com.deepak.AirBnB.dto.HotelDto;
import com.deepak.AirBnB.dto.HotelSearchRequest;
import com.deepak.AirBnB.entity.Hotel;
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
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ModelMapper modelMapper;

    @Override
    public void initializeRoomForAYear(Room room) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusYears(1);

        for(;!today.isAfter(endDate);today=today.plusDays(1)) {
            Inventory inventory = Inventory.builder()
                    .city(room.getHotel().getCity())
                    .hotel(room.getHotel())
                    .date(today)
                    .room(room)
                    .price(room.getBasePrice())
                    .bookedCount(0)
                    .reservedCount(0)
                    .totalCount(room.getTotalCount())
                    .surgeFactor(BigDecimal.ONE)
                    .closed(false)
                    .build();

            inventoryRepository.save(inventory);
        };

    }

    @Override
    public void deleteInventories(Room room) {
        log.info("Deleting the inventories of room with id: {}", room.getId());
        LocalDate today = LocalDate.now();
        inventoryRepository.deleteByRoom(room);
    }

    @Override
    public Page<HotelDto> searchHotels(HotelSearchRequest hotelSearchRequest) {
     log.info("Searching hotels for {} city, from {} to {}",
             hotelSearchRequest.getCity(),hotelSearchRequest.getStartDate(), hotelSearchRequest.getEndDate());
        Pageable pageable = PageRequest.of(hotelSearchRequest.getPage(), hotelSearchRequest.getSize());
        long dateCount= ChronoUnit.DAYS.between(hotelSearchRequest.getStartDate(), hotelSearchRequest.getEndDate())+1;
        Page<Hotel> page= inventoryRepository.findHotelsWithAvailableInventory(hotelSearchRequest.getCity(),
                hotelSearchRequest.getStartDate(),hotelSearchRequest.getEndDate(),hotelSearchRequest.getRoomCount(),dateCount,pageable);

        return page.map(hotel -> modelMapper.map(hotel, HotelDto.class));
    }


}
