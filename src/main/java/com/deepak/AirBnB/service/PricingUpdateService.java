package com.deepak.AirBnB.service;

import com.deepak.AirBnB.entity.Hotel;
import com.deepak.AirBnB.entity.HotelMinPrice;
import com.deepak.AirBnB.entity.Inventory;
import com.deepak.AirBnB.repository.HotelMinPriceRepository;
import com.deepak.AirBnB.repository.HotelRepository;
import com.deepak.AirBnB.repository.InventoryRepository;
import com.deepak.AirBnB.strategy.PricingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PricingUpdateService {

private final HotelRepository hotelRepository;
private final InventoryRepository inventoryRepository;
private final PricingService pricingService;
private final HotelMinPriceRepository hotelMinPriceRepository;



//    @Scheduled(cron="*/5 * * * * *")
   public void updatePrices() {

       int page=0;
       int batchSize=100;

       while(true){
           Page<Hotel> hotelPage= hotelRepository.findAll(PageRequest.of(page,batchSize));
           if(hotelPage.isEmpty()){
               break;
           }

           hotelPage.getContent().forEach(hotel -> {
               updateHotelPrices(hotel);
           });
           page++;
       }
   }

   public void updateHotelPrices(Hotel hotel) {
       log.info("Updating hotel prices for hotel ID: {}", hotel.getId());
       LocalDate startDate = LocalDate.now();
       LocalDate endDate = LocalDate.now().plusYears(1);
       List<Inventory> inventoryList= inventoryRepository.findByHotelAndDateBetween(hotel,startDate,endDate);
       updateInventoryPrices(inventoryList);
       updateHotelMinPrice(hotel,inventoryList,startDate,endDate);
   }

    private void updateHotelMinPrice(Hotel hotel,List<Inventory> inventoryList,LocalDate startDate,LocalDate endDate) {
       log.info("Updating Hotel Min prices for inventory IDs: {}", inventoryList.size());
        // Compute minimum price per day for the hotel
        Map<LocalDate, BigDecimal> dailyMinPrices = inventoryList.stream()
                .collect(Collectors.groupingBy(
                        Inventory::getDate,
                        Collectors.mapping(Inventory::getPrice, Collectors.minBy(Comparator.naturalOrder()))
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().orElse(BigDecimal.ZERO)));

        // Prepare HotelPrice entities in bulk
        List<HotelMinPrice> hotelPrices = new ArrayList<>();
        dailyMinPrices.forEach((date, price) -> {
            HotelMinPrice hotelPrice = hotelMinPriceRepository.findByHotelAndDate(hotel, date)
                    .orElse(new HotelMinPrice(hotel, date));
            hotelPrice.setPrice(price);
            hotelPrices.add(hotelPrice);
        });

        // Save all HotelPrice entities in bulk
        hotelMinPriceRepository.saveAll(hotelPrices);
    }

    public void updateInventoryPrices(List<Inventory> inventoryList) {
       log.info("Updating inventory prices for inventory IDs: {}", inventoryList.size());
       inventoryList.forEach(inventory -> {
           BigDecimal price = pricingService.calculateDynamicPrice(inventory);
           inventory.setPrice(price);
       });
       inventoryRepository.saveAll(inventoryList);
    }
}
