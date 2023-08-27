package com.usernotificationservicerest.controllers;

import com.usernotificationservicerest.entites.User;
import com.usernotificationservicerest.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User user1;

    private User user2;

    @BeforeEach
    public void setUp() {
        user1 = new User();
        user1.setId(1L);
        user1.setFullName("Ivanov Ivan Ivanovich");

        user2 = new User();
        user2.setId(2L);
        user2.setFullName("Petrov Peter Petrovich");
    }

    @Test
    void getUserById_success() {
        given(userService.findUserById(1L)).willReturn(Optional.of(user1));

        ResponseEntity<User> responseEntity = userController.getUserById(1L);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(user1, responseEntity.getBody());
    }

    @Test
    void getUserById_notFound() {
        given(userService.findUserById(3L)).willReturn(Optional.empty());

        ResponseEntity<User> responseEntity = userController.getUserById(3L);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    void getUsers_success() {
        given(userService.findAll()).willReturn(Arrays.asList(user1, user2));

        ResponseEntity<List<User>> responseEntity = userController.getUsers();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(2, responseEntity.getBody().size());
    }

    @Test
    void createUser_success() {
        User inputUser = new User();
        inputUser.setFullName("Vladimir Vladimirovich");
        User createdUser = new User();
        createdUser.setId(3L);
        createdUser.setFullName("Aleskandrov Aleksandr");

        given(userService.createUser(inputUser)).willReturn(createdUser);

        ResponseEntity<User> responseEntity = userController.createUser(inputUser);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(createdUser, responseEntity.getBody());
    }

    @Test
    void updateUser_success() {
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setFullName("Nokilai");

        given(userService.updateUser(updatedUser)).willReturn(updatedUser);

        ResponseEntity<User> responseEntity = userController.updateUser(1L, updatedUser);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(updatedUser, responseEntity.getBody());
    }

    @Test
    void updateUser_illegalArgument() {
        User updatedUser = new User();
        updatedUser.setId(3L);
        updatedUser.setFullName("John");

        given(userService.updateUser(updatedUser)).willThrow(IllegalArgumentException.class);

        ResponseEntity<User> responseEntity = userController.updateUser(3L, updatedUser);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    void updateUser_mismatchedId() {
        User updatedUser = new User();
        updatedUser.setId(2L);
        updatedUser.setFullName("Donald");

        ResponseEntity<User> responseEntity = userController.updateUser(1L, updatedUser);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void deleteUser_success() {
        given(userService.findUserById(1L)).willReturn(Optional.of(user1));

        ResponseEntity<Void> responseEntity = userController.deleteUser(1L);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(userService, times(1)).deleteUser(user1);
    }

    @Test
    void deleteUser_notFound() {
        given(userService.findUserById(3L)).willReturn(Optional.empty());

        ResponseEntity<Void> responseEntity = userController.deleteUser(3L);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }
}

