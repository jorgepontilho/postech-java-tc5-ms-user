package com.postech.msuser.gateway;

import com.postech.msuser.dto.UserDTO;
import com.postech.msuser.entity.User;
import com.postech.msuser.exceptions.NotFoundException;
import com.postech.msuser.interfaces.IUserGateway;
import com.postech.msuser.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserGateway implements IUserGateway {
    private final UserRepository userRepository;

    public UserGateway(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDTO createUser(UserDTO userDTO) {
        User userNew = new User(userDTO);
        String encryptedPassword = new BCryptPasswordEncoder().encode(userNew.getPassword());
        userNew.setPassword(encryptedPassword);
        userNew.setPasswordConfirmation(encryptedPassword);
        userNew = userRepository.save(userNew);
        return userNew.toDTO();
    }

    public UserDTO updateUser(UserDTO userDTO) {
        User userNew = new User(userDTO);
        String encryptedPassword = new BCryptPasswordEncoder().encode(userNew.getPassword());
        userNew.setPassword(encryptedPassword);
        userNew.setPasswordConfirmation(encryptedPassword);
        return userRepository.save(userNew).toDTO();
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
    public UserDTO findById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado")).toDTO();
    }

    public List<UserDTO> listAll() {
        List<User> customerList = userRepository.findAll();
        return customerList
                .stream()
                .map(this::toUserDTO)
                .collect(Collectors.toList());
    }

    private UserDTO toUserDTO(User user) {
        return user.toDTO();
    }

    @Override
    public UserDTO findByLoginAndPassword(String login, String password) {

        try {
            PasswordEncoder encoder = new BCryptPasswordEncoder();
            User user = userRepository.findByLogin(login);
            if (encoder.matches(password, user.getPassword())) {
                return user.toDTO();
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public UserDTO findByLogin(String login) {
        try {
            return userRepository.findByLogin(login).toDTO();
        } catch (Exception e) {
            return null;
        }
    }

}
