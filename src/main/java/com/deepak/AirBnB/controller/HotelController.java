package com.deepak.AirBnB.controller;

import com.deepak.AirBnB.dto.BookingDto;
import com.deepak.AirBnB.dto.HotelDto;
import com.deepak.AirBnB.dto.HotelReportDto;
import com.deepak.AirBnB.service.BookingService;
import com.deepak.AirBnB.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/admin/hotels")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<HotelDto> createHotel(@RequestBody HotelDto hotelDto) {
        HotelDto hotel= hotelService.createNewHotel(hotelDto);
        return ResponseEntity.ok().body(hotel);
    }

    @GetMapping("/{hotelId}")
    public ResponseEntity<HotelDto> getHotelById(@PathVariable Long hotelId) {
        return ResponseEntity.ok().body(hotelService.getHotelById(hotelId));
    }

    @PutMapping("/{hotelId}")
    public ResponseEntity<HotelDto> updateHotel(@RequestBody HotelDto hotelDto,@PathVariable Long hotelId) {
        return ResponseEntity.ok().body(hotelService.updateHotel(hotelDto, hotelId));
    }

    @DeleteMapping("/{hotelId}")
    public ResponseEntity<Void> deleteHotel(@PathVariable Long hotelId) {
    hotelService.deleteHotel(hotelId);
    return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{hotelId}")
    public ResponseEntity<Void> activateHotel(@PathVariable Long hotelId) {
        hotelService.activateHotel(hotelId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<HotelDto>> getAllHotels() {
        return ResponseEntity.ok().body(hotelService.getAllHotels());
    }

    @GetMapping("/{hotelId}/bookings")
    public ResponseEntity<List<BookingDto>> getAllBookingsByHotelId(@PathVariable Long hotelId) {
        return ResponseEntity.ok().body(bookingService.getAllBookingsByHotelId(hotelId));
    }

    @GetMapping("/{hotelId}/reports")
    public ResponseEntity<HotelReportDto> getHotelReport(@PathVariable Long hotelId,
                                                               @RequestParam(required = false) LocalDate startDate,
                                                               @RequestParam(required = false) LocalDate endDate) {

        if(startDate == null)  startDate = LocalDate.now().minusMonths(1);
        if(endDate == null)    endDate = LocalDate.now();


        return ResponseEntity.ok().body(bookingService.getHotelReport(hotelId,startDate,endDate));

    }


}
