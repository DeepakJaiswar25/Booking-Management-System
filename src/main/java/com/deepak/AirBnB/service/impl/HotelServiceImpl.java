package com.deepak.AirBnB.service.impl;
import com.deepak.AirBnB.dto.BookingDto;
import com.deepak.AirBnB.dto.HotelDto;
import com.deepak.AirBnB.dto.HotelInfoDto;
import com.deepak.AirBnB.dto.RoomDto;
import com.deepak.AirBnB.entity.Booking;
import com.deepak.AirBnB.entity.Hotel;
import com.deepak.AirBnB.entity.Room;
import com.deepak.AirBnB.entity.User;
import com.deepak.AirBnB.exception.UnAuthorisedException;
import com.deepak.AirBnB.repository.BookingRepository;
import com.deepak.AirBnB.repository.HotelRepository;
import com.deepak.AirBnB.repository.RoomRepository;
import com.deepak.AirBnB.service.HotelService;
import com.deepak.AirBnB.service.InventoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.deepak.AirBnB.utils.AppUtils.getCurrentUser;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;
    private final InventoryService inventoryService;
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

    @Override
    public HotelDto createNewHotel(HotelDto hotelDto) {
        log.info("creating New Hotel with hotel Name {}", hotelDto.getName());
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Hotel hotel =modelMapper.map(hotelDto, Hotel.class);
        hotel.setActive(false);
        hotel.setOwner(user);
        hotel=hotelRepository.save(hotel);
        log.info("Hotel Created with hotel Name {}", hotel.getName());
        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    public HotelDto updateHotel(HotelDto hotelDto,Long hotelId) {

        log.info("updating Hotel with hotel Name {}", hotelDto.getName());
        Hotel hotel =hotelRepository.findById(hotelId).orElseThrow(()->new RuntimeException("No Hotel Found with this Id "+hotelId));
        modelMapper.map(hotelDto, hotel);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(!user.equals(hotel.getOwner())) {
            throw new UnAuthorisedException("This user does not own this hotel with id: "+hotel.getId());
        }
        hotel.setId(hotelId);
        hotel=hotelRepository.save(hotel);
        log.info("Hotel Updated with hotel Name {}", hotel.getName());
        return modelMapper.map(hotel, HotelDto.class);

    }

    @Override
    public void deleteHotel(Long id) {
        log.info("deleting Hotel with hotel Id {}", id);
        Hotel hotel =hotelRepository.findById(id).orElseThrow(()->new RuntimeException("No Hotel Found with this Id "+id));
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(!user.equals(hotel.getOwner())) {
            throw new UnAuthorisedException("This user does not own this hotel with id: "+id);
        }

        for(Room room: hotel.getRooms()) {
            inventoryService.deleteInventories(room);
            roomRepository.deleteById(room.getId());
        }
        hotelRepository.deleteById(id);
    }

    @Override
    public HotelDto getHotelById(Long id) {
        log.info("getting Hotel with hotel Id {}", id);
        Hotel hotel =hotelRepository.findById(id).orElseThrow(()->new RuntimeException("No Hotel Found with this Id "+id));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(!user.equals(hotel.getOwner())) {
            throw new UnAuthorisedException("This user does not own this hotel with id: "+id);
        }
        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    public void activateHotel(Long id) {
        log.info("activating Hotel with hotel Id {}", id);
        Hotel hotel =hotelRepository.findById(id).orElseThrow(()->new RuntimeException("No Hotel Found with this Id "+id));
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(!user.equals(hotel.getOwner())) {
            throw new UnAuthorisedException("This user does not own this hotel with id: "+id);
        }
        hotel.setActive(true);
        for(Room room: hotel.getRooms()) {
            inventoryService.initializeRoomForAYear(room);
        }
    }

    @Override
    public HotelInfoDto getHotelInfoById(Long hotelId) {
        log.info("getting Hotel with hotel Id {}", hotelId);
        Hotel hotel =hotelRepository.findById(hotelId).orElseThrow(()->new RuntimeException("No Hotel Found with this Id "+hotelId));

        List<RoomDto> rooms = hotel.getRooms().stream()
                .map(room -> modelMapper.map(room, RoomDto.class)).toList();
        return new HotelInfoDto(modelMapper.map(hotel,HotelDto.class),rooms);
    }

    @Override
    public List<HotelDto> getAllHotels() {
        User user = getCurrentUser();
        log.info("getting All Hotels for user with id {}", user.getId());
        List<Hotel> hotels = hotelRepository.findByOwner(user);
        return hotels.stream()
                .map(hotel -> modelMapper.map(hotel, HotelDto.class))
                .collect(Collectors.toList());
    }

}
