package com.deepak.AirBnB.service;

import com.deepak.AirBnB.dto.GuestDto;

import java.util.List;

public interface GuestService {

    List<GuestDto> getAllGuests();

    void updateGuest(Long guestId, GuestDto guestDto);

    void deleteGuest(Long guestId);

    GuestDto addNewGuest(GuestDto guestDto);
}
