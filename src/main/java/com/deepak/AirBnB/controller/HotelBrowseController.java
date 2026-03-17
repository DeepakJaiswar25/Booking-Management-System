package com.deepak.AirBnB.controller;

import com.deepak.AirBnB.dto.HotelDto;
import com.deepak.AirBnB.dto.HotelInfoDto;
import com.deepak.AirBnB.dto.HotelPriceDto;
import com.deepak.AirBnB.dto.HotelSearchRequest;
import com.deepak.AirBnB.service.HotelService;
import com.deepak.AirBnB.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
public class HotelBrowseController {

    private final HotelService hotelService;
    private final InventoryService inventoryService;

    @GetMapping("/search")
    public ResponseEntity<Page<HotelPriceDto>> searchHotels(@RequestBody HotelSearchRequest hotelSearchRequest) {
        Page<HotelPriceDto> page=inventoryService.searchHotels(hotelSearchRequest);
        return ResponseEntity.ok().body(page);
    }

    @GetMapping("/{hotelId}/info")
    public ResponseEntity<HotelInfoDto> getHotelInfo(@PathVariable Long hotelId) {

       return ResponseEntity.ok().body(hotelService.getHotelInfoById(hotelId));

    }
}
