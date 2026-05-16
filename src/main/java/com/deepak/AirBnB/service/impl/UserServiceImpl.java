package com.deepak.AirBnB.service.impl;

import com.deepak.AirBnB.dto.ProfileUpdateRequestDto;
import com.deepak.AirBnB.dto.UserDto;
import com.deepak.AirBnB.entity.User;
import com.deepak.AirBnB.exception.ResourceNotFoundException;
import com.deepak.AirBnB.repository.UserRepository;
import com.deepak.AirBnB.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.deepak.AirBnB.utils.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService , UserDetailsService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElse(null);
    }


    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with id: "+id));
    }

    @Override
    public void updateProfile(ProfileUpdateRequestDto userProfileUpdateRequest) {
    User user = getCurrentUser();
    if(userProfileUpdateRequest.getGender() != null) user.setGender(userProfileUpdateRequest.getGender());
    if(userProfileUpdateRequest.getName() != null) user.setName(userProfileUpdateRequest.getName());
    if(userProfileUpdateRequest.getDateOfBirth() != null) user.setDateOfBirth(userProfileUpdateRequest.getDateOfBirth());

    userRepository.save(user);
    }

    @Override
    public UserDto getMyProfile() {
        User user = getCurrentUser();
        log.info("Getting the profile for user with id: {}", user.getId());
        return modelMapper.map(user, UserDto.class);

    }


}
