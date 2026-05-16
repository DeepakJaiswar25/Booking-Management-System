package com.deepak.AirBnB.service;

import com.deepak.AirBnB.dto.ProfileUpdateRequestDto;
import com.deepak.AirBnB.dto.UserDto;
import com.deepak.AirBnB.entity.User;

public interface UserService {

    User getUserById(Long id);

    void updateProfile(ProfileUpdateRequestDto userProfileUpdateRequest);

    UserDto getMyProfile();
}
