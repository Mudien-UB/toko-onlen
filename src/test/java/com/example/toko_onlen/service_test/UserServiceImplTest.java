package com.example.toko_onlen.service_test;


import com.example.toko_onlen.model.entity.User;
import com.example.toko_onlen.repository.UserRepository;
import com.example.toko_onlen.service.impl.UserServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

    class UserServiceImplTest {

        private UserRepository userRepository;
        private UserServiceImpl userService;

        @BeforeEach
        void setUp() {
            userRepository = mock(UserRepository.class);
            userService = new UserServiceImpl(userRepository);
        }

        @Test
        void testAddUser_Success() {
            String username = "validuser";
            User user = User.builder().username(username).build();
            when(userRepository.save(any(User.class))).thenReturn(user);

            User savedUser = userService.addUser(username);

            assertEquals(username, savedUser.getUsername());
            verify(userRepository, times(1)).save(any(User.class));
        }

        @Test
        void testAddUser_BlankUsername_ShouldThrow() {
            assertThrows(IllegalArgumentException.class, () -> {
                userService.addUser(" ");
            });
        }

        @Test
        void testAddUser_ShortUsername_ShouldThrow() {
            assertThrows(IllegalArgumentException.class, () -> {
                userService.addUser("abc");
            });
        }

        @Test
        void testGetUserById_Found() {
            UUID id = UUID.randomUUID();
            User user = User.builder().id(id).username("testuser").build();
            when(userRepository.findById(id)).thenReturn(Optional.of(user));

            User result = userService.getUserById(id);

            assertNotNull(result);
            assertEquals("testuser", result.getUsername());
        }

        @Test
        void testGetUserById_NotFound_ShouldThrow() {
            UUID id = UUID.randomUUID();
            when(userRepository.findById(id)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> {
                userService.getUserById(id);
            });
        }

        @Test
        void testUpdateUser_Success() {
            UUID id = UUID.randomUUID();
            String newUsername = "updatedUser";

            User user = User.builder().id(id).username("oldUser").build();
            when(userRepository.findById(id)).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

            User result = userService.updateUser(id, newUsername);

            assertEquals(newUsername, result.getUsername());
            verify(userRepository).save(user);
        }

        @Test
        void testDeleteUserById_Success() {
            UUID id = UUID.randomUUID();
            User user = User.builder().id(id).username("todelete").build();
            when(userRepository.findById(id)).thenReturn(Optional.of(user));

            userService.deleteUserById(id);

            verify(userRepository).delete(user);
        }

        @Test
        void testGetUserByUsername_Success() {
            User user = User.builder().username("testuser").build();
            when(userRepository.findByUsername("testuser")).thenReturn(user);

            User result = userService.getUserByUsername("testuser");

            assertEquals("testuser", result.getUsername());
        }

        @Test
        void testGetUserByUsername_NotFound_ShouldThrow() {
            when(userRepository.findByUsername("notfound")).thenReturn(null);

            assertThrows(EntityNotFoundException.class, () -> {
                userService.getUserByUsername("notfound");
            });
        }
        @Test
        @DisplayName("Update User - Null ID should throw IllegalArgumentException")
        void updateUser_NullId_ShouldThrow() {
            assertThrows(IllegalArgumentException.class, () -> {
                userService.updateUser(null, "validusername");
            });
        }

        @Test
        @DisplayName("Update User - Blank Username should throw IllegalArgumentException")
        void updateUser_BlankUsername_ShouldThrow() {
            UUID id = UUID.randomUUID();
            assertThrows(IllegalArgumentException.class, () -> {
                userService.updateUser(id, "  ");
            });
        }

        @Test
        @DisplayName("Update User - Username length less than 5 should throw IllegalArgumentException")
        void updateUser_ShortUsername_ShouldThrow() {
            UUID id = UUID.randomUUID();
            assertThrows(IllegalArgumentException.class, () -> {
                userService.updateUser(id, "abc");
            });
        }

        @Test
        @DisplayName("Update User - User not found should throw EntityNotFoundException")
        void updateUser_UserNotFound_ShouldThrow() {
            UUID id = UUID.randomUUID();
            when(userRepository.findById(id)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> {
                userService.updateUser(id, "validusername");
            });
        }

        @Test
        @DisplayName("Get User by Username - Blank Username should throw IllegalArgumentException")
        void getUserByUsername_BlankUsername_ShouldThrow() {
            assertThrows(IllegalArgumentException.class, () -> {
                userService.getUserByUsername(" ");
            });
        }

        @Test
        @DisplayName("Delete User - Null ID should throw IllegalArgumentException")
        void deleteUserById_NullId_ShouldThrow() {
            assertThrows(IllegalArgumentException.class, () -> {
                userService.deleteUserById(null);
            });
        }

        @Test
        @DisplayName("Delete User - User not found should throw EntityNotFoundException")
        void deleteUserById_UserNotFound_ShouldThrow() {
            UUID id = UUID.randomUUID();
            when(userRepository.findById(id)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> {
                userService.deleteUserById(id);
            });
        }
    }

