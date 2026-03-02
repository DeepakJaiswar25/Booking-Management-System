package com.deepak.AirBnB.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Embeddable
public class HotelContactInfo {
    private String address;
    private String location;
    private String email;
    private String phoneNumber;
}
