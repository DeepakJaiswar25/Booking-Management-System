package com.deepak.AirBnB.controller;

import com.deepak.AirBnB.dto.BookingDto;
import com.deepak.AirBnB.dto.BookingRequest;
import com.deepak.AirBnB.dto.GuestDto;
import com.deepak.AirBnB.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @PostMapping("/{bookingId}/payment")
    public ResponseEntity<Map<String,String>> initiatePayment(@PathVariable Long bookingId) {
        String sessionUrl=bookingService.initiatePayments(bookingId);
        return ResponseEntity.ok().body(Map.of("sessionUrl",sessionUrl));

    }

    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<Void> cancelPayment(@PathVariable Long bookingId) {
        bookingService.cancelPayment(bookingId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{bookingId}/status")
    public ResponseEntity<Map<String,String>> getBookingStatus(@PathVariable Long bookingId) {
        String status= bookingService.getBookingStatus(bookingId);
        return ResponseEntity.ok().body(Map.of("status",status));
    }


}
