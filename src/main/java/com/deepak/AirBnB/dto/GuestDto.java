package com.deepak.AirBnB.dto;

import com.deepak.AirBnB.entity.User;
import com.deepak.AirBnB.enums.Gender;
import lombok.Data;

@Data
public class GuestDto {
    private Long id;
    private User user;
    private String name;
    private Gender gender;
    private Integer age;
}
