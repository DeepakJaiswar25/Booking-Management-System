package com.deepak.AirBnB.service;

import com.deepak.AirBnB.dto.HotelDto;
import com.deepak.AirBnB.dto.HotelPriceDto;
import com.deepak.AirBnB.dto.HotelSearchRequest;
import com.deepak.AirBnB.entity.Room;
import org.springframework.data.domain.Page;

public interface InventoryService {

    void initializeRoomForAYear(Room room);

    void deleteInventories(Room room);

    Page<HotelPriceDto> searchHotels(HotelSearchRequest hotelSearchRequest);

}
