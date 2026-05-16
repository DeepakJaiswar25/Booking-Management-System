package com.deepak.AirBnB.service;

import com.deepak.AirBnB.dto.BookingDto;
import com.deepak.AirBnB.dto.HotelDto;
import com.deepak.AirBnB.dto.HotelInfoDto;

import java.util.List;

public interface HotelService {


    HotelDto createNewHotel(HotelDto hotelDto);

    HotelDto updateHotel(HotelDto hotelDto, Long id);

    void deleteHotel(Long id);

    HotelDto getHotelById(Long id);

    void activateHotel(Long id);

    HotelInfoDto getHotelInfoById(Long hotelId);

    List<HotelDto> getAllHotels();
}
