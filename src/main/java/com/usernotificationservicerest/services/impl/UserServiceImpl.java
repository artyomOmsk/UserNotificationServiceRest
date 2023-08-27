package com.usernotificationservicerest.services.impl;

import com.usernotificationservicerest.entites.User;
import com.usernotificationservicerest.repositories.UserRepository;
import com.usernotificationservicerest.services.UserService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<User> findAll() {
        return repository.findAll();
    }

    @Override
    public User createUser(User user) {
        return repository.save(user);
    }

    @Override
    public User updateUser(User user) {
        if (user.getId() == null || !repository.existsById(user.getId())) {
            throw new IllegalArgumentException("User does not exist");
        }
        return repository.save(user);
    }

    @Override
    public void deleteUser(User user) {
        repository.delete(user);
    }
}
