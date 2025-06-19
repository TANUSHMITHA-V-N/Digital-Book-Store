package com.example.demo.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.User;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<String> saveUser(@RequestBody User user) {
        logger.info("Received request to create a new user.");
        String message = userService.saveUser(user);
        logger.info("User created successfully.");
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        logger.info("Fetching all users.");
        List<User> users = userService.getAllUsers();
        logger.info("Retrieved {} users.", users.size());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        logger.info("Fetching user by ID: {}", id);
        try {
            User user = userService.getUserById(id);
            logger.info("User found with ID: {}", id);
            return ResponseEntity.ok(user);
        } catch (UserNotFoundException e) {
            logger.warn("User not found with ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user) {
        logger.info("Updating user with ID: {}", id);
        try {
            user.setId(id);
            User updated = userService.updateUser(user);
            logger.info("User updated successfully with ID: {}", id);
            return ResponseEntity.ok(updated);
        } catch (UserNotFoundException e) {
            logger.warn("User not found for update with ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

   
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        logger.info("Deleting user with ID: {}", id);
        try {
            String result = userService.deleteUserById(id);
            logger.info("User deleted successfully with ID: {}", id);
            return ResponseEntity.ok(result);
        } catch (UserNotFoundException e) {
            logger.warn("User not found for deletion with ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
