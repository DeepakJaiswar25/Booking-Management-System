package com.deepak.AirBnB.dto;

import com.deepak.AirBnB.enums.Gender;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ProfileUpdateRequestDto {

    private String name;
    private LocalDate dateOfBirth;
    private Gender gender;

}
