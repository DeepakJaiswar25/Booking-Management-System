package com.deepak.AirBnB.service.impl;

import com.deepak.AirBnB.dto.*;
import com.deepak.AirBnB.entity.*;
import com.deepak.AirBnB.enums.BookingStatus;
import com.deepak.AirBnB.exception.ResourceNotFoundException;
import com.deepak.AirBnB.repository.*;
import com.deepak.AirBnB.service.BookingService;
import com.deepak.AirBnB.service.HotelService;
import com.deepak.AirBnB.service.RoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final BookingRepository bookingRepository;
    private final ModelMapper modelMapper;
    private final GuestRepository guestRepository;
    private final InventoryRepository inventoryRepository;

    @Override
    @Transactional
    public BookingDto initBooking(BookingRequest bookingRequest) {
        log.info("Initialising booking for hotel : {}, room: {}, date {}-{}", bookingRequest.getHotelId(),
                bookingRequest.getRoomId(), bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate());
        Hotel hotel= hotelRepository.findById(bookingRequest.getHotelId())
                .orElseThrow(()->new RuntimeException("No Hotel Found with this Id "+bookingRequest.getHotelId()));
        Room room = roomRepository.findById(bookingRequest.getRoomId())
                .orElseThrow(()->new RuntimeException("No Hotel Found with this Id "+bookingRequest.getRoomId()));

        List<Inventory> inventoryList= inventoryRepository.findAndLockAvailableInventory(bookingRequest.getRoomId()
                ,bookingRequest.getCheckInDate(),bookingRequest.getCheckOutDate(),bookingRequest.getRoomsCount());
        long dayCount= ChronoUnit.DAYS.between(bookingRequest.getCheckInDate(),bookingRequest.getCheckOutDate())+1;
        if(inventoryList.size()!=dayCount) {
        throw new IllegalStateException("Room is not available anymore");
        }
        for(Inventory inventory: inventoryList) {
            inventory.setReservedCount(inventory.getReservedCount()+bookingRequest.getRoomsCount());
        }
        inventoryRepository.saveAll(inventoryList);
        Booking booking = Booking.builder()
                .bookingStatus(BookingStatus.RESERVED)
                .hotel(hotel)
                .room(room)
                .checkInDate(bookingRequest.getCheckInDate())
                .checkOutDate(bookingRequest.getCheckOutDate())
                .user(getCurrentUser())
                .roomsCount(bookingRequest.getRoomsCount())
                .amount(BigDecimal.TEN)
                .build();
        booking = bookingRepository.save(booking);
        return modelMapper.map(booking, BookingDto.class);
    }

    @Override
    public BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(()->new ResourceNotFoundException("No Booking found with this Id "+bookingId));
        if(isBookingExpired(booking)){
            throw new IllegalStateException("Booking is expired");
        }
        if(booking.getBookingStatus()!=BookingStatus.RESERVED){
            throw new IllegalStateException("Booking status is not RESERVED, cannot add guests");
        }
        for(GuestDto guestDto: guestDtoList) {
            Guest guest=modelMapper.map(guestDto,Guest.class);
            guest.setUser(getCurrentUser());
            guest=guestRepository.save(guest);
            booking.getGuests().add(guest);
        }
        booking.setBookingStatus(BookingStatus.GUESTS_ADDED);
        booking=bookingRepository.save(booking);
        return modelMapper.map(booking, BookingDto.class);
    }

    public boolean isBookingExpired(Booking booking) {
        return  booking.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now());
    }

    public User getCurrentUser() {
        User user = new User();
        user.setId(1L);
        return user;
    }
}
