package com.deepak.AirBnB.service;

import com.deepak.AirBnB.dto.BookingDto;
import com.deepak.AirBnB.dto.BookingRequest;
import com.deepak.AirBnB.dto.GuestDto;

import java.util.List;

public interface BookingService {
    BookingDto initBooking(BookingRequest bookingRequest);

    BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList);
}
