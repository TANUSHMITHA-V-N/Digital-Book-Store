package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.model.User;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public String saveUser(User user) {
        logger.info("Saving new user with email: {}", user.getEmail());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
            logger.debug("No role provided. Defaulting role to USER.");
        }
        repository.save(user);
        logger.info("User saved successfully with ID: {}", user.getId());
        return "User saved successfully!";
    }

    @Override
    public User updateUser(User user) {
        logger.info("Attempting to update user with ID: {}", user.getId());
        Optional<User> existingUserOpt = repository.findById(user.getId());
        if (existingUserOpt.isPresent()) {
            User existing = existingUserOpt.get();

            existing.setName(user.getName() != null ? user.getName() : existing.getName());
            existing.setEmail(user.getEmail() != null ? user.getEmail() : existing.getEmail());
            existing.setRole(user.getRole() != null ? user.getRole() : existing.getRole());

            User updated = repository.save(existing);
            logger.info("User updated successfully with ID: {}", updated.getId());
            return updated;
        } else {
            logger.error("User not found for update with ID: {}", user.getId());
            throw new UserNotFoundException("User not found for update with ID: " + user.getId());
        }
    }

    @Override
    public User getUserById(Long userId) {
        logger.info("Fetching user with ID: {}", userId);
        return repository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User not found with ID: {}", userId);
                    return new UserNotFoundException("User not found with ID: " + userId);
                });
    }

    @Override
    public String deleteUserById(Long userId) {
        logger.info("Attempting to delete user with ID: {}", userId);
        if (repository.existsById(userId)) {
            repository.deleteById(userId);
            logger.info("User deleted successfully with ID: {}", userId);
            return "User deleted successfully!";
        } else {
            logger.error("User not found for deletion with ID: {}", userId);
            throw new UserNotFoundException("User not found for deletion with ID: " + userId);
        }
    }

    @Override
    public List<User> getAllUsers() {
        logger.info("Retrieving all users.");
        return repository.findAll();
    }
}
