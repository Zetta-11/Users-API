package com.klimmenkov.testtask.service.impl;

import com.klimmenkov.testtask.model.User;
import com.klimmenkov.testtask.repository.UserRepository;
import com.klimmenkov.testtask.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

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
        Optional<User> existingUserOptional = userRepository.findById(userId);
        if (existingUserOptional.isPresent()) {
            User existingUser = existingUserOptional.get();

            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setFirstName(updatedUser.getFirstName());
            existingUser.setLastName(updatedUser.getLastName());
            existingUser.setBirthDate(updatedUser.getBirthDate());
            existingUser.setAddress(updatedUser.getAddress());
            existingUser.setPhoneNumber(updatedUser.getPhoneNumber());

            return userRepository.save(existingUser);
        } else {
            return null;
        }
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
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
