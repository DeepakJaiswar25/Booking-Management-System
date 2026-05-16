package com.deepak.AirBnB.controller;

import com.deepak.AirBnB.dto.BookingDto;
import com.deepak.AirBnB.dto.GuestDto;
import com.deepak.AirBnB.dto.ProfileUpdateRequestDto;
import com.deepak.AirBnB.dto.UserDto;
import com.deepak.AirBnB.service.BookingService;
import com.deepak.AirBnB.service.GuestService;
import com.deepak.AirBnB.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final BookingService bookingService;
    private final GuestService guestService;

    @PatchMapping("/profile")
    public ResponseEntity<Void> updateUserProfile(@RequestBody ProfileUpdateRequestDto userProfileUpdateRequest) {

        userService.updateProfile(userProfileUpdateRequest);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/myBookings")
    public ResponseEntity<List<BookingDto>> getMyBookings() {
        return ResponseEntity.ok().body(bookingService.getMyBookings());
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDto> getMyProfile() {
        return ResponseEntity.ok().body(userService.getMyProfile());
    }

    @GetMapping("/guests")
    public ResponseEntity<List<GuestDto>> getAllGuests() {
        return ResponseEntity.ok(guestService.getAllGuests());
    }

    @PostMapping("/guests")
    public ResponseEntity<GuestDto> addNewGuest(@RequestBody GuestDto guestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(guestService.addNewGuest(guestDto));
    }

    @PutMapping("guests/{guestId}")
    public ResponseEntity<Void> updateGuest(@PathVariable Long guestId, @RequestBody GuestDto guestDto) {
        guestService.updateGuest(guestId, guestDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("guests/{guestId}")
    public ResponseEntity<Void> deleteGuest(@PathVariable Long guestId) {
        guestService.deleteGuest(guestId);
        return ResponseEntity.noContent().build();
    }
}
