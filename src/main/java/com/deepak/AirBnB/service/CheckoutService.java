package com.deepak.AirBnB.service;

import com.deepak.AirBnB.entity.Booking;

public interface CheckoutService {

    String getCheckoutUrl(Booking booking, String successUrl, String failureUrl);
}
