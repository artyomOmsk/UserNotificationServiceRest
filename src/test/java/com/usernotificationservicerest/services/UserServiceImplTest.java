package com.usernotificationservicerest.services;

import com.usernotificationservicerest.entites.InformationPeriod;
import com.usernotificationservicerest.entites.User;
import com.usernotificationservicerest.repositories.UserRepository;
import com.usernotificationservicerest.services.impl.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {

        InformationPeriod userInformationPeriod = new InformationPeriod();
        userInformationPeriod.setId(1L);
        userInformationPeriod.setDayOfWeek(LocalDateTime.now().getDayOfWeek());
        userInformationPeriod.setStartTime(LocalTime.now().minusHours(1));
        userInformationPeriod.setEndTime(LocalTime.now().plusHours(1));
        user = new User();
        user.setId(1L);
        user.setFullName("Ivanov Ivan Ivanovich");
        user.setInformationPeriods(List.of(userInformationPeriod));
    }

    @Test
    void findUserById_userExtends_shouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Optional<User> userOptional = userService.findUserById(1L);
        Assertions.assertEquals(user, userOptional.orElse(null));
    }

    @Test
    void findUserById_userDoesNotExists_shouldReturnNull() {
        when(userRepository.findById(3L)).thenReturn(Optional.empty());
        Optional<User> userOptional = userService.findUserById(3L);
        Assertions.assertNull(userOptional.orElse(null));
    }

    @Test
    void findAll_shouldReturnUsersList() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        List<User> users = userService.findAll();
        Assertions.assertEquals(1, users.size());
    }

    @Test
    void createUser_validUser_shouldSaveUser() {
        when(userRepository.save(user)).thenReturn(user);
        User savedUser = userRepository.save(user);
        Assertions.assertEquals(user, savedUser);
    }

    @Test
    void createUser_withNullField_shouldThrowException() {
        when(userRepository.save(ArgumentMatchers.any(User.class))).thenThrow(IllegalArgumentException.class);
        User user = new User();
        Assertions.assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));
    }

    @Test
    void updateUser_withCorrectFields_shouldUpdateUser() {
        user.setFullName("Test Fullname");
        when(userRepository.save(user)).thenReturn(user);
        when(userRepository.existsById(user.getId())).thenReturn(true);
        User updatedUser = userService.updateUser(user);
        Assertions.assertEquals(user, updatedUser);
    }

    @Test
    void updateUser_withNullFields_shouldThrowException() {
        user.setFullName(null);
        Assertions.assertThrows(IllegalArgumentException.class, () -> userService.updateUser(user));
    }

    @Test
    void deleteUser_userExists_shouldDeleteUser() {
        userService.deleteUser(user);
        verify(userRepository, times(1)).delete(user);
    }

}
