package com.deepak.AirBnB.service.impl;

import com.deepak.AirBnB.dto.RoomDto;
import com.deepak.AirBnB.entity.Hotel;
import com.deepak.AirBnB.entity.Room;
import com.deepak.AirBnB.repository.HotelRepository;
import com.deepak.AirBnB.repository.RoomRepository;
import com.deepak.AirBnB.service.RoomService;
import jakarta.persistence.ManyToOne;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final ModelMapper modelMapper;
    private final HotelRepository hotelRepository;

    @Override
    public RoomDto createNewRoom(Long hotelId, RoomDto roomDto) {
        log.info("Creating room with hotelId {}", hotelId);
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("No Hotel Found with this Id " + hotelId));
        Room room = modelMapper.map(roomDto, Room.class);
        room.setHotel(hotel);
        room = roomRepository.save(room);
        return modelMapper.map(room, RoomDto.class);
    }

    @Override
    public List<RoomDto> getAllRoomsInHotel(Long hotelId) {
        log.info("Getting all rooms in hotel {}", hotelId);
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("No Hotel Found with this Id " + hotelId));
        List<Room> rooms = hotel.getRooms();
        return rooms.stream()
                .map(room -> modelMapper.map(room, RoomDto.class)).collect(Collectors.toList());
    }

    @Override
    public RoomDto getRoomById(Long roomId) {
        log.info("Getting room with roomId {}", roomId);
        Room room= roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("No Room Found with this Id " + roomId));
        return modelMapper.map(room, RoomDto.class);
    }

    @Override
    public void deleteRoomById(Long roomId) {
        log.info("Deleting room with roomId {}", roomId);
        Room room= roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("No Room Found with this Id " + roomId));
        roomRepository.deleteById(roomId);
    }
}
