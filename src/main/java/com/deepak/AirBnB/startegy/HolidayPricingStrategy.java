package com.deepak.AirBnB.startegy;

import com.deepak.AirBnB.entity.Inventory;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class HolidayPricingStrategy implements PricingStrategy {

    private final PricingStrategy wrapped;
    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal price = wrapped.calculatePrice(inventory);
        boolean isHolidayToday= true;
        if(isHolidayToday){
            price =price.multiply(BigDecimal.valueOf(1.25));
        }
        return price;
    }
}
