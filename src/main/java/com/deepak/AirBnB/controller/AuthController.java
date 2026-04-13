package com.deepak.AirBnB.controller;

import com.deepak.AirBnB.dto.LoginRequestDto;
import com.deepak.AirBnB.dto.LoginResponseDto;
import com.deepak.AirBnB.dto.SignUpRequestDto;
import com.deepak.AirBnB.dto.UserDto;
import com.deepak.AirBnB.security.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signUp(@RequestBody SignUpRequestDto signUpRequestDto){
        return new  ResponseEntity<>(authService.signUp(signUpRequestDto), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletRequest request, HttpServletResponse response){
        String[] tokens= authService.login(loginRequestDto);

        Cookie cookie=new Cookie("refreshToken",tokens[1]);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

        return ResponseEntity.ok(new LoginResponseDto(tokens[0]));

    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refresh(HttpServletRequest request){

        String refreshToken = Arrays.stream(request.getCookies())
                        .filter(cookie -> "refreshToken".equals(cookie.getName()))
                        .findFirst()
                        .map(Cookie::getValue)
                        .orElseThrow(() ->new AuthenticationServiceException("Refresh token not found inside the Cookies"));

        String token = authService.refreshToken(refreshToken);

        return new  ResponseEntity<>(new LoginResponseDto(token),HttpStatus.OK);
    }
}
