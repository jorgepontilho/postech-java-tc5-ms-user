package com.postech.msuser.gateway;

import com.postech.msuser.entity.User;
import com.postech.msuser.interfaces.IUserGateway;
import com.postech.msuser.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserGateway implements IUserGateway {
    private final UserRepository userRepository;

    public UserGateway(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(User user) {
        String encryptedPassword = new BCryptPasswordEncoder().encode(user.getPassword());
        user.setPassword(encryptedPassword);
        user.setPasswordConfirmation(encryptedPassword);
        return userRepository.save(user);
    }

    @Override
    public User updateUser(User user) {
        String encryptedPassword = new BCryptPasswordEncoder().encode(user.getPassword());
        user.setPassword(encryptedPassword);
        user.setPasswordConfirmation(encryptedPassword);
        return userRepository.save(user);
    }

    @Override
    public boolean deleteUser(Integer id) {
        try {
            userRepository.deleteById(id);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public User findById(Integer id) {
        try {
            return userRepository.findById(id).orElseThrow();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public User findByLogin(String login) {
        try {
            return userRepository.findByLogin(login);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<User> listAllUsers() {
        return userRepository.findAll();
    }
}
