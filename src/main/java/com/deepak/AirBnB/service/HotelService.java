package com.deepak.AirBnB.service;

import com.deepak.AirBnB.dto.HotelDto;

public interface HotelService {


    HotelDto createNewHotel(HotelDto hotelDto);

    HotelDto updateHotel(HotelDto hotelDto, Long id);

    void deleteHotel(Long id);

    HotelDto getHotelById(Long id);

    void activateHotel(Long id);

}
