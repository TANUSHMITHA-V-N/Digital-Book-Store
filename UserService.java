package com.example.demo.service;

import java.util.List;
import com.example.demo.model.User;

public interface UserService {

    String saveUser(User user);

    User updateUser(User user);

    User getUserById(Long userid);

    String deleteUserById(Long userid);

    List<User> getAllUsers();
}
