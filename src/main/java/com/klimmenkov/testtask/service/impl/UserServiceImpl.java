package com.klimmenkov.testtask.service.impl;

import com.klimmenkov.testtask.exception.UserNotFoundException;
import com.klimmenkov.testtask.model.User;
import com.klimmenkov.testtask.repository.UserRepository;
import com.klimmenkov.testtask.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long userId, User updatedUser) {
        User userToUpdate = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        userToUpdate.setEmail(updatedUser.getEmail());
        userToUpdate.setFirstName(updatedUser.getFirstName());
        userToUpdate.setLastName(updatedUser.getLastName());
        userToUpdate.setBirthDate(updatedUser.getBirthDate());
        userToUpdate.setAddress(updatedUser.getAddress());
        userToUpdate.setPhoneNumber(updatedUser.getPhoneNumber());

        return userRepository.save(userToUpdate);
    }

    @Override
    public User partiallyUpdateUser(Long userId, Map<String, Object> updates) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        try {
            for (Map.Entry<String, Object> entry : updates.entrySet()) {
                String fieldName = entry.getKey();
                Object fieldValue = entry.getValue();

                Field field = User.class.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(existingUser, fieldValue);
            }
            return userRepository.save(existingUser);

        } catch (Exception e) {
            throw new RuntimeException("Failed to update user with ID: " + userId, e);
        }
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public List<User> searchUsersByBirthDateRange(Date fromDate, Date toDate) {
        return userRepository.findByBirthDateBetween(fromDate, toDate);
    }
}
