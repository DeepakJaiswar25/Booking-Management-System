package com.deepak.AirBnB.dto;

import com.deepak.AirBnB.entity.Hotel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelPriceDto {

    private Hotel hotel;
    private Double price;
}
