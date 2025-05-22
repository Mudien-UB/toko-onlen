package com.example.toko_onlen.controller;

import com.example.toko_onlen.dto.common.CustomizeResponseEntity;
import com.example.toko_onlen.dto.request.UserRequest;
import com.example.toko_onlen.dto.response.UserResponse;
import com.example.toko_onlen.dto.validation.OnUpdate;
import com.example.toko_onlen.exception.BadRequestException;
import com.example.toko_onlen.service.UserService;
import com.example.toko_onlen.util.UuidParseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/user")
@Validated
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/create-user")
    public ResponseEntity<?> createUser(@Validated(OnUpdate.class) @RequestBody UserRequest userRequest) {
        return CustomizeResponseEntity.buildResponse(HttpStatus.CREATED, "User created", UserResponse.of(userService.addUser(userRequest.getUsername())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable String id) {

        if(!UuidParseUtil.isValidUUID(id)) {
            throw new BadRequestException("Invalid UUID");
        }
        return CustomizeResponseEntity.buildResponse(
                HttpStatus.OK,
                "user found",
                userService.getUserById(UUID.fromString(id))
        );
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        if(username.isBlank()){
            throw new BadRequestException("Invalid username");
        }
        return CustomizeResponseEntity.buildResponse(
                HttpStatus.OK,
                "user found",
                userService.getUserByUsername(username)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable String id, @Validated(OnUpdate.class) @RequestBody UserRequest userRequest) {
        if(!UuidParseUtil.isValidUUID(id)) {
            throw new BadRequestException("Invalid UUID");
        }
        return CustomizeResponseEntity.buildResponse(HttpStatus.OK, "User updated", UserResponse.of(userService.updateUser(UUID.fromString(id), userRequest.getUsername())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {

        userService.deleteUserById(UuidParseUtil.stringToUuid(id));

        return CustomizeResponseEntity.buildResponse(
                HttpStatus.OK,
                "User Deleted",
                null
        );
    }

}
