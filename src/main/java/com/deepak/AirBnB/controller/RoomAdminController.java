package com.deepak.AirBnB.controller;

import com.deepak.AirBnB.dto.RoomDto;
import com.deepak.AirBnB.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/hotels/{hotelId}/rooms")
@RequiredArgsConstructor
public class RoomAdminController {

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<RoomDto> createRoom(@RequestBody RoomDto roomDto, @PathVariable Long hotelId) {
        RoomDto room= roomService.createNewRoom(hotelId, roomDto);
        return  ResponseEntity.ok().body(room);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomDto> getRoomById(@PathVariable Long hotelId, @PathVariable Long roomId) {
        RoomDto room = roomService.getRoomById(roomId);
        return  ResponseEntity.ok().body(room);
    }

    @GetMapping
    public ResponseEntity<List<RoomDto>> getAllRooms(@PathVariable Long hotelId) {
        List<RoomDto> rooms =roomService.getAllRoomsInHotel(hotelId);
        return ResponseEntity.ok().body(rooms);
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long hotelId, @PathVariable Long roomId) {
        roomService.deleteRoomById(roomId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{roomId}")
    public ResponseEntity<RoomDto> updateRoom(@PathVariable Long hotelId, @PathVariable Long roomId, @RequestBody RoomDto roomDto) {
        RoomDto updatedRoom = roomService.updateRoomById(hotelId, roomId, roomDto);
        return ResponseEntity.ok().body(updatedRoom);
    }

}
