package com.deepak.AirBnB.service;

import com.deepak.AirBnB.dto.BookingDto;
import com.deepak.AirBnB.dto.BookingRequest;
import com.deepak.AirBnB.dto.GuestDto;
import com.deepak.AirBnB.dto.HotelReportDto;
import com.deepak.AirBnB.entity.Booking;
import com.stripe.model.Event;

import java.time.LocalDate;
import java.util.List;

public interface BookingService {
    BookingDto initBooking(BookingRequest bookingRequest);

    BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList);

    String initiatePayments(Long bookingId);

    void capturePayment(Event event);

    void cancelPayment(Long bookingId);

    String getBookingStatus(Long bookingId);


    List<BookingDto> getAllBookingsByHotelId(Long hotelId);

    HotelReportDto getHotelReport(Long hotelId, LocalDate startDate, LocalDate endDate);

    List<BookingDto> getMyBookings();
}
