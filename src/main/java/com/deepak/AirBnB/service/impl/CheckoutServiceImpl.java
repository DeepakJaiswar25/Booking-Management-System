package com.deepak.AirBnB.service.impl;

import com.deepak.AirBnB.entity.Booking;
import com.deepak.AirBnB.enums.BookingStatus;
import com.deepak.AirBnB.repository.BookingRepository;
import com.deepak.AirBnB.service.CheckoutService;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {

    private final BookingRepository bookingRepository;
    @Override
    public String getCheckoutUrl(Booking booking, String successUrl, String failureUrl) {

        try {
            Customer customer= Customer.create(
                    CustomerCreateParams.builder()
                            .setEmail(booking.getUser().getEmail())
                            .setName(booking.getUser().getName())
                            .build()
            );

            SessionCreateParams sessionCreateParams= SessionCreateParams.builder()
                    .setCustomer(customer.getId())
                    .setSuccessUrl(successUrl)
                    .setCancelUrl(failureUrl)
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("usd")
                                                    .setUnitAmount(booking.getAmount().multiply(BigDecimal.valueOf(100)).longValue())
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName(booking.getHotel().getName() + " : " + booking.getRoom().getType())
                                                                    .setDescription("Booking ID: " + booking.getId())
                                                                    .build()
                                            ).build()
                                    ).build()
                    ).build();
            Session session= Session.create(sessionCreateParams);
            booking.setPaymentSessionId(session.getId());
            bookingRepository.save(booking);

            return session.getUrl();


        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }
}
