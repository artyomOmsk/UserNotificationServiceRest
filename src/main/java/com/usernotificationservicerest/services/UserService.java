package com.usernotificationservicerest.services;


import com.usernotificationservicerest.entites.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    Optional<User> findUserById(Long id);

    List<User> findAll();

    User createUser(User user);

    User updateUser(User user);

    void deleteUser(User user);
}
