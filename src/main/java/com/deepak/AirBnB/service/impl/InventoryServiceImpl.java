package com.deepak.AirBnB.service.impl;

import com.deepak.AirBnB.dto.HotelPriceDto;
import com.deepak.AirBnB.dto.HotelSearchRequest;
import com.deepak.AirBnB.dto.InventoryDto;
import com.deepak.AirBnB.dto.UpdateInventoryRequestDto;
import com.deepak.AirBnB.entity.Inventory;
import com.deepak.AirBnB.entity.Room;
import com.deepak.AirBnB.entity.User;
import com.deepak.AirBnB.exception.ResourceNotFoundException;
import com.deepak.AirBnB.exception.UnAuthorisedException;
import com.deepak.AirBnB.repository.HotelMinPriceRepository;
import com.deepak.AirBnB.repository.InventoryRepository;
import com.deepak.AirBnB.repository.RoomRepository;
import com.deepak.AirBnB.service.InventoryService;
import com.deepak.AirBnB.strategy.PricingService;
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
import java.util.List;
import java.util.stream.Collectors;

import static com.deepak.AirBnB.utils.AppUtils.getCurrentUser;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final HotelMinPriceRepository hotelMinPriceRepository;
    private final ModelMapper modelMapper;
    private final RoomRepository roomRepository;
    private final PricingService pricingService;

    @Override
    public void initializeRoomForAYear(Room room) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusYears(1);

        for (; !today.isAfter(endDate); today = today.plusDays(1)) {
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
        }
        ;

    }

    @Override
    public void deleteInventories(Room room) {
        log.info("Deleting the inventories of room with id: {}", room.getId());
        LocalDate today = LocalDate.now();
        inventoryRepository.deleteByRoom(room);
    }

    @Override
    public Page<HotelPriceDto> searchHotels(HotelSearchRequest hotelSearchRequest) {
        log.info("Searching hotels for {} city, from {} to {}",
                hotelSearchRequest.getCity(), hotelSearchRequest.getStartDate(), hotelSearchRequest.getEndDate());
        Pageable pageable = PageRequest.of(hotelSearchRequest.getPage(), hotelSearchRequest.getSize());
        long dateCount = ChronoUnit.DAYS.between(hotelSearchRequest.getStartDate(), hotelSearchRequest.getEndDate()) + 1;

        Page<HotelPriceDto> page = hotelMinPriceRepository.findHotelsWithAvailableInventory(hotelSearchRequest.getCity(),
                hotelSearchRequest.getStartDate(), hotelSearchRequest.getEndDate(), hotelSearchRequest.getRoomCount(), dateCount, pageable);

        return page;
    }

    @Override
    public List<InventoryDto> getAllInventory(Long roomId) {
        log.info("Getting inventory for room with id: {}", roomId);
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + roomId));
        User user = getCurrentUser();
        if (!room.getHotel().getOwner().equals(user)) {
            throw new UnAuthorisedException("You are not the owner of room with id: " + roomId);
        }
        return inventoryRepository.findByRoomOrderByDate(room).stream()
                .map((element) -> modelMapper.map(element, InventoryDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateInventory(Long roomId, UpdateInventoryRequestDto updateInventoryRequestDto) {
        log.info("Updating inventory for room with id: {}", roomId);
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + roomId));
        User user = getCurrentUser();
        if (!room.getHotel().getOwner().equals(user)) {
            throw new UnAuthorisedException("You are not the owner of room with id: " + roomId);
        }
        inventoryRepository.getInventoryAndLockBeforeUpdate(roomId, updateInventoryRequestDto.getStartDate(),
                updateInventoryRequestDto.getEndDate());
        inventoryRepository.updateInventory(roomId, updateInventoryRequestDto.getStartDate(), updateInventoryRequestDto.getEndDate(),
                updateInventoryRequestDto.getClosed(), updateInventoryRequestDto.getSurgeFactor());
    }

    @Override
    public void updateBasePriceForFutureInventories(Room room, BigDecimal newBasePrice) {
        log.info("Updating base price for future inventories of room with id: {}", room.getId());
        LocalDate today = LocalDate.now();
        List<Inventory> futureInventories = inventoryRepository.findByRoomAndDateGreaterThanEqual(room, today);
        // Apply new base price on room for dynamic pricing calculation and recalculate each inventory price
        futureInventories.forEach(inventory -> {
            // Temporarily update the room base price so pricing strategies pick it up
            if (inventory.getRoom() != null) {
                inventory.getRoom().setBasePrice(newBasePrice);
            }
            // Recalculate dynamic price using PricingService which composes pricing strategies
            try {
                BigDecimal updatedPrice = pricingService.calculateDynamicPrice(inventory);
                inventory.setPrice(updatedPrice);
            } catch (Exception e) {
                log.error("Failed to calculate dynamic price for inventory {}: {}. Falling back to base price.", inventory.getId(), e.getMessage());
                inventory.setPrice(newBasePrice);
            }
        });

        inventoryRepository.saveAll(futureInventories);
    }
}