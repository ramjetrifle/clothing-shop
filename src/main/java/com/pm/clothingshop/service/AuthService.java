package com.pm.clothingshop.service;

import com.pm.clothingshop.dto.request.LoginRequest;
import com.pm.clothingshop.dto.request.UserRegisterRequest;
import com.pm.clothingshop.dto.response.AuthResponse;
import com.pm.clothingshop.dto.response.UserResponse;
import com.pm.clothingshop.exception.UnauthorizedException;
import com.pm.clothingshop.model.User;
import com.pm.clothingshop.repository.UserRepository;
import com.pm.clothingshop.security.JwtUtil;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public AuthService(UserRepository userRepository,
                       UserService userService,
                       JwtUtil jwtUtil,
                       PasswordEncoder passwordEncoder,
                       ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }
    public AuthResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByUsernameOrEmail(loginRequest.getUsernameOrEmail(),
                loginRequest.getUsernameOrEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }
        String token = jwtUtil.generateToken(user.getEmail());
        UserResponse userResponse = modelMapper.map(user, UserResponse.class);

        return new AuthResponse(token, userResponse);
    }
    public AuthResponse register(UserRegisterRequest userRegisterRequest) {
        UserResponse userResponse = userService.registerUser(userRegisterRequest);
        String token = jwtUtil.generateToken(userResponse.getEmail());
        return new AuthResponse(token, userResponse);
    }
    public boolean validateToken(String token) {
        try {
            return jwtUtil.validateToken(token);
        } catch (Exception e) {
            return false;
        }
    }
    public UserResponse getUserFromToken(String token) {
        String email = jwtUtil.extractUsername(token);
        User user = userService.findUserByEmail(email);
        return modelMapper.map(user, UserResponse.class);
    }
}
