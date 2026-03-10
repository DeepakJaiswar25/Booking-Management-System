package com.deepak.AirBnB.controller;

import com.deepak.AirBnB.dto.BookingDto;
import com.deepak.AirBnB.dto.BookingRequest;
import com.deepak.AirBnB.dto.GuestDto;
import com.deepak.AirBnB.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class HotelBookingController {

    private final BookingService bookingService;

    @PostMapping("/init")
    public ResponseEntity<BookingDto> initBooking(@RequestBody BookingRequest bookingRequest) {
        return ResponseEntity.ok().body(bookingService.initBooking(bookingRequest));
    }

    @PostMapping("/{bookingId}/addGuests")
    public ResponseEntity<BookingDto> addGuests(@PathVariable Long bookingId, @RequestBody List<GuestDto> guestDtoList) {
        return ResponseEntity.ok().body(bookingService.addGuests(bookingId,guestDtoList));

    }


}
