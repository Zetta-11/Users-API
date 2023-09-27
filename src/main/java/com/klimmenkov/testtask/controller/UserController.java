package com.klimmenkov.testtask.controller;

import com.klimmenkov.testtask.exception.AgeNotAllowedException;
import com.klimmenkov.testtask.exception.UserNotFoundException;
import com.klimmenkov.testtask.model.User;
import com.klimmenkov.testtask.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    @Value("${user.minAge}")
    private int minAge;

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody @Valid User user) {
        LocalDate birthDate = user.getBirthDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate currentDate = LocalDate.now();

        if (Period.between(birthDate, currentDate).getYears() < minAge) {
            throw new AgeNotAllowedException("User's age is not allowed. Must be more than 18 years");
        }

        User createdUser = userService.createUser(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<User> partiallyUpdateUser(@PathVariable Long userId, @RequestBody Map<String, Object> updates) {
        User existingUser = userService.getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        try {
            for (Map.Entry<String, Object> entry : updates.entrySet()) {
                String fieldName = entry.getKey();
                Object fieldValue = entry.getValue();

                Field field = User.class.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(existingUser, fieldValue);
            }
            User updatedUser = userService.updateUser(userId, existingUser);

            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable Long userId, @RequestBody User updatedUser) {
        User user = userService.updateUser(userId, updatedUser);

        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            throw new UserNotFoundException("User not found with ID: " + userId);
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsersByBirthDateRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate) {

        if (fromDate.after(toDate)) {
            return ResponseEntity.badRequest().build();
        }

        List<User> users = userService.searchUsersByBirthDateRange(fromDate, toDate);
        return ResponseEntity.ok(users);
    }
}
