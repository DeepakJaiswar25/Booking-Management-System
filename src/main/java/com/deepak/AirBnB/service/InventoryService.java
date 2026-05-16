package com.deepak.AirBnB.service;

import com.deepak.AirBnB.dto.*;
import com.deepak.AirBnB.entity.Room;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

public interface InventoryService {

    void initializeRoomForAYear(Room room);

    void deleteInventories(Room room);

    Page<HotelPriceDto> searchHotels(HotelSearchRequest hotelSearchRequest);

    List<InventoryDto> getAllInventory(Long roomId);

    void updateInventory(Long roomId, UpdateInventoryRequestDto updateInventoryRequestDto);

    void updateBasePriceForFutureInventories(Room room, BigDecimal newBasePrice);
}
