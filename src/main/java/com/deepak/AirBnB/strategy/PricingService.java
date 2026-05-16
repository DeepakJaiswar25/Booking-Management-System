package com.deepak.AirBnB.strategy;

import com.deepak.AirBnB.entity.Inventory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PricingService {

    public BigDecimal calculateDynamicPrice(Inventory inventory){
        PricingStrategy pricingStrategy= new BasePricingStrategy();

        pricingStrategy= new SurgePricingStrategy(pricingStrategy);
        pricingStrategy= new OccupancyPricingStrategy(pricingStrategy);
        pricingStrategy = new UrgencyPricingStrategy(pricingStrategy);
        pricingStrategy = new HolidayPricingStrategy(pricingStrategy);

        return pricingStrategy.calculatePrice(inventory);
    }

    public BigDecimal calculatePriceForBooking(List<Inventory> inventoryList){
       return inventoryList.stream()
               .map(this::calculateDynamicPrice)
               .reduce(BigDecimal.ZERO, BigDecimal::add);
    }



}
