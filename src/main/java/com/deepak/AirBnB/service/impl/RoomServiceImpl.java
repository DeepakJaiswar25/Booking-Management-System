package com.deepak.AirBnB.service.impl;

import com.deepak.AirBnB.dto.RoomDto;
import com.deepak.AirBnB.entity.Hotel;
import com.deepak.AirBnB.entity.Room;
import com.deepak.AirBnB.entity.User;
import com.deepak.AirBnB.exception.UnAuthorisedException;
import com.deepak.AirBnB.repository.HotelRepository;
import com.deepak.AirBnB.repository.RoomRepository;
import com.deepak.AirBnB.service.InventoryService;
import com.deepak.AirBnB.service.RoomService;
import jakarta.persistence.ManyToOne;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final InventoryService inventoryService;

    @Override
    public RoomDto createNewRoom(Long hotelId, RoomDto roomDto) {
        log.info("Creating room with hotelId {}", hotelId);
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("No Hotel Found with this Id " + hotelId));
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getOwner())) {
            throw new UnAuthorisedException("This user does not own this hotel with id: "+hotelId);
        }
        Room room = modelMapper.map(roomDto, Room.class);
        room.setHotel(hotel);
        room = roomRepository.save(room);
        if(hotel.getActive()){
            inventoryService.initializeRoomForAYear(room);
        }
        return modelMapper.map(room, RoomDto.class);
    }

    @Override
    public List<RoomDto> getAllRoomsInHotel(Long hotelId) {
        log.info("Getting all rooms in hotel {}", hotelId);
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("No Hotel Found with this Id " + hotelId));
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getOwner())) {
            throw new UnAuthorisedException("This user does not own this hotel with id: "+hotelId);
        }

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
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(room.getHotel().getOwner())) {
            throw new UnAuthorisedException("This user does not own this room with id: "+roomId);
        }
        roomRepository.deleteById(roomId);
    }
}
