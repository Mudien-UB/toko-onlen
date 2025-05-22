package com.example.toko_onlen.service;

import com.example.toko_onlen.model.entity.User;

import java.util.UUID;

public interface UserService {

    User addUser(String username);

    User updateUser(UUID id, String Username);

    User getUserById(UUID id);

    User getUserByUsername(String username);

    void deleteUserById(UUID id);
}
