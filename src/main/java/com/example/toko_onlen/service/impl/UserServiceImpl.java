package com.example.toko_onlen.service.impl;

import com.example.toko_onlen.model.entity.User;
import com.example.toko_onlen.repository.UserRepository;
import com.example.toko_onlen.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User addUser(String username) {
        if(username.isBlank()){
            throw new IllegalArgumentException("Username cannot be null");
        }else if(username.length() < 5){
            throw new IllegalArgumentException("Username length cannot be less than 5");
        }
        User newUser = User.builder()
                        .username(username)
                        .build();

        return userRepository.save(newUser);
    }

    @Override
    public User updateUser(UUID id, String username) {

        if(id == null || username.isBlank()){
            throw new IllegalArgumentException("require parameters");
        }else if(username.length() < 5){
            throw new IllegalArgumentException("Username length cannot be less than 5");
        }

        User  user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        user.setUsername(username);
        return userRepository.save(user);
    }

    @Override
    public User getUserById(UUID id) {
        return  userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Override
    public User getUserByUsername(String username) {

        if(username.isBlank()){
            throw new IllegalArgumentException("Username cannot be null");
        }

        User user = userRepository.findByUsername(username);
        if(user == null){
            throw new EntityNotFoundException("User not found");
        }
        return user;
    }

    @Override
    public void deleteUserById(UUID id) {
        if(id == null){
            throw new IllegalArgumentException("Id cannot be null");
        }
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
        userRepository.delete(user);
    }
}
