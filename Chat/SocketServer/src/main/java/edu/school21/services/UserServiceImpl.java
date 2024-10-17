package edu.school21.services;

import edu.school21.models.User;
import edu.school21.repositories.UsersRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class UserServiceImpl implements UserService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UsersRepository usersRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<User> registerUser(String username, String password) {

        Optional<User> existingUser = usersRepository.findByName(username);
        if (existingUser.isPresent()) {
            return Optional.empty();
        }

        String hashedPassword = passwordEncoder.encode(password);

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(hashedPassword);

        usersRepository.save(newUser);

        return Optional.of(newUser);
    }


    @Override
    public Optional<User> loginUser(String username, String password) {
        Optional<User> dataBaseUser = usersRepository.findByName(username);
        if (dataBaseUser.isPresent()) {
            User user = dataBaseUser.get();
            if(passwordEncoder.matches(password, user.getPassword())){
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return usersRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return usersRepository.findByName(username);
    }
}
