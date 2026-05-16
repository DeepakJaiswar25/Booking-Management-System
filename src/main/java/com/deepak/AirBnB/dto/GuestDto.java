package com.deepak.AirBnB.dto;

import com.deepak.AirBnB.entity.User;
import com.deepak.AirBnB.enums.Gender;
import lombok.Data;

import java.time.LocalDate;

@Data
public class GuestDto {
    private Long id;
    private User user;
    private String name;
    private Gender gender;
    private LocalDate dateOfBirth;
}
