package edu.school21.services;


import edu.school21.models.User;

import java.util.Optional;

public interface UserService {
    Optional<User> registerUser(String password, String username);
    Optional<User> loginUser(String username, String password);
    Optional<User> getUserById(Long id);
    Optional<User> getUserByUsername(String username);
}
