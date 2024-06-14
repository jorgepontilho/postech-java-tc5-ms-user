package com.postech.msuser.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.postech.msuser.dto.UserDTO;
import com.postech.msuser.entity.User;
import com.postech.msuser.exceptions.NotFoundException;
import com.postech.msuser.interfaces.IUserGateway;
import com.postech.msuser.repository.UserRepository;
import com.postech.msuser.security.SecurityUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class UserGateway implements IUserGateway {
    private final UserRepository userRepository;
    static RestTemplate restTemplate = new RestTemplate();
    private static String msUserUrl;

    public UserGateway(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Value("${api.msuser.url}")
    public void setMsUserUrl(String msUserUrl) {
        this.msUserUrl = msUserUrl;
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
        Optional<User> user = userRepository.findById(id);
        return (user.map(User::toDTO).orElse(null));
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

    public static SecurityUser getUserFromToken(String token) {
        SecurityUser securityUser= new SecurityUser();
        try {
            String url = msUserUrl + "/" + token;
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> map = mapper.readValue(response.getBody(), Map.class);
            securityUser.setLogin( map.get("login").toString());
            securityUser.setRole( map.get("role").toString());
            return securityUser;
        } catch (Exception e) {
            return null;
        }
    }
}
