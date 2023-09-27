package com.klimmenkov.testtask.service;

import com.klimmenkov.testtask.model.User;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(User user);

    User updateUser(Long userId, User updatedUser);

    Optional<User> getUserById(Long userId);

    List<User> getAllUsers();

    void deleteUser(Long userId);

    List<User> searchUsersByBirthDateRange(Date fromDate, Date toDate);

}
