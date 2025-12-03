package com.pm.clothingshop.service;

import com.pm.clothingshop.dto.request.UserRegisterRequest;
import com.pm.clothingshop.dto.response.UserResponse;
import com.pm.clothingshop.enums.Role;
import com.pm.clothingshop.exception.DuplicateResourceException;
import com.pm.clothingshop.exception.ResourceNotFoundException;
import com.pm.clothingshop.exception.UnauthorizedException;
import com.pm.clothingshop.model.Cart;
import com.pm.clothingshop.model.User;
import com.pm.clothingshop.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       ModelMapper modelMapper,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }
    @Transactional
    public UserResponse registerUser(UserRegisterRequest userRegisterRequest) {
        if (userRepository.existsByEmail(userRegisterRequest.getEmail())) {
            throw new DuplicateResourceException("User", "email", userRegisterRequest.getEmail());
        }

        if (userRepository.existsByUsername(userRegisterRequest.getUsername())) {
            throw new DuplicateResourceException("User", "email", userRegisterRequest.getEmail());
        }

        User user = new User();
        user.setUsername(userRegisterRequest.getUsername());
        user.setEmail(userRegisterRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRegisterRequest.getPassword()));
        user.setRole(Role.CUSTOMER);

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setTotalPrice(0.0);
        user.setCart(cart);

        User savedUser = userRepository.save(user);

        return modelMapper.map(savedUser, UserResponse.class);
    }
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return modelMapper.map(user, UserResponse.class);
    }
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", email));
        return modelMapper.map(user, UserResponse.class);
    }
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(user -> modelMapper.map(user, UserResponse.class))
                .toList();
    }
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", email));
    }
    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("User not authonticated"));
    }
    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }
    public void deleteUserById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        userRepository.deleteById(id);
    }
}
