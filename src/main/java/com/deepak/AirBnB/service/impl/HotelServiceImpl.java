package com.deepak.AirBnB.service.impl;
import com.deepak.AirBnB.dto.HotelDto;
import com.deepak.AirBnB.entity.Hotel;
import com.deepak.AirBnB.entity.Room;
import com.deepak.AirBnB.repository.HotelRepository;
import com.deepak.AirBnB.service.HotelService;
import com.deepak.AirBnB.service.InventoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;
    private final InventoryService inventoryService;

    @Override
    public HotelDto createNewHotel(HotelDto hotelDto) {
        log.info("creating New Hotel with hotel Name {}", hotelDto.getName());
        Hotel hotel =modelMapper.map(hotelDto, Hotel.class);
        hotel.setActive(false);
        hotel=hotelRepository.save(hotel);
        log.info("Hotel Created with hotel Name {}", hotel.getName());
        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    public HotelDto updateHotel(HotelDto hotelDto,Long hotelId) {

        log.info("updating Hotel with hotel Name {}", hotelDto.getName());
        Hotel hotel =hotelRepository.findById(hotelId).orElseThrow(()->new RuntimeException("No Hotel Found with this Id "+hotelId));
        modelMapper.map(hotelDto, hotel);
        hotel.setId(hotelId);
        hotel=hotelRepository.save(hotel);
        log.info("Hotel Updated with hotel Name {}", hotel.getName());
        return modelMapper.map(hotel, HotelDto.class);

    }

    @Override
    public void deleteHotel(Long id) {
        log.info("deleting Hotel with hotel Id {}", id);
        Hotel hotel =hotelRepository.findById(id).orElseThrow(()->new RuntimeException("No Hotel Found with this Id "+id));
        hotelRepository.deleteById(id);
        for(Room room: hotel.getRooms()) {
            inventoryService.deleteFutureInventories(room);
        }
    }

    @Override
    public HotelDto getHotelById(Long id) {
        log.info("getting Hotel with hotel Id {}", id);
        Hotel hotel =hotelRepository.findById(id).orElseThrow(()->new RuntimeException("No Hotel Found with this Id "+id));
        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    public void activateHotel(Long id) {
        Hotel hotel =hotelRepository.findById(id).orElseThrow(()->new RuntimeException("No Hotel Found with this Id "+id));
        hotel.setActive(true);
        for(Room room: hotel.getRooms()) {
            inventoryService.initializeRoomForAYear(room);
        }
    }
}
