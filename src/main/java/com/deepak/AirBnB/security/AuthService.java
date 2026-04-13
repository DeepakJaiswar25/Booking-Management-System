package com.deepak.AirBnB.security;

import com.deepak.AirBnB.dto.LoginRequestDto;
import com.deepak.AirBnB.dto.SignUpRequestDto;
import com.deepak.AirBnB.dto.UserDto;
import com.deepak.AirBnB.entity.User;
import com.deepak.AirBnB.enums.Role;
import com.deepak.AirBnB.exception.ResourceNotFoundException;
import com.deepak.AirBnB.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;


    public UserDto signUp(SignUpRequestDto signUpRequestDto){
       User user= userRepository.findByEmail(signUpRequestDto.getEmail()).orElse(null);
       if(user!=null){
           throw new RuntimeException("User is already present with same email id");
       }
       User newUser=modelMapper.map(signUpRequestDto,User.class);
       newUser.setRoles(Set.of(Role.GUEST));
       newUser.setPassword(passwordEncoder.encode(signUpRequestDto.getPassword()));
       User savedUser=userRepository.save(newUser);
       return modelMapper.map(savedUser,UserDto.class);
    }

    public String[] login(LoginRequestDto loginRequestDto){
        Authentication authentication= authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequestDto.getEmail(),loginRequestDto.getPassword()
        ));
        User user= (User) authentication.getPrincipal();
        String accessToken= jwtService.generateAccessToken(user);
        String refreshToken= jwtService.generateRefreshToken(user);
        return new String[]{accessToken,refreshToken};
    }

    public String refreshToken(String refreshToken){
        Long id = jwtService.getUserIdFromToken(refreshToken);

        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with id: "+id));

        return jwtService.generateRefreshToken(user);
    }

}
