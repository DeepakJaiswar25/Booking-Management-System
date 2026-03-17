package com.deepak.AirBnB.strategy;

import com.deepak.AirBnB.entity.Inventory;

import java.math.BigDecimal;

public interface PricingStrategy {

    BigDecimal calculatePrice(Inventory inventory);
}
