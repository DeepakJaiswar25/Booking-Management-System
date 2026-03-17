package com.deepak.AirBnB.startegy;

import com.deepak.AirBnB.entity.Inventory;

import java.math.BigDecimal;

public interface PricingStrategy {

    BigDecimal calculatePrice(Inventory inventory);
}
