package com.klimmenkov.testtask.service;

import com.klimmenkov.testtask.model.User;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface UserService {
    User createUser(User user);

    User updateUser(Long userId, User updatedUser);

    User partiallyUpdateUser(Long userId, Map<String, Object> updates);

    void deleteUser(Long userId);

    List<User> searchUsersByBirthDateRange(Date fromDate, Date toDate);

}
