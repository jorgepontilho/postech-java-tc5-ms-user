package com.postech.msuser.gateway;

import com.postech.msuser.entity.User;
import com.postech.msuser.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.postech.msuser.util.UserUtilTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class UserGatewayTest {
    @InjectMocks
    private UserGateway userGateway;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUser_ValidInput_ReturnsUser() {
        User user = UserUtilTest.createUser();
        when(userRepository.save(user)).thenReturn(user);
        User result = userGateway.createUser(user);
        assertEquals(user, result);
    }

    @Test
    void testUpdateUser_ValidInput_ReturnsUser() {
        User user = new User();
        when(userRepository.save(user)).thenReturn(user);
        User result = userGateway.updateUser(user);
        assertEquals(user, result);
    }

    @Test
    void testDeleteUser_ValidInput_ReturnsTrue() {
        Random random = new Random();
        int id = random.nextInt();
        doNothing().when(userRepository).deleteById(id);
        boolean result = userGateway.deleteUser(id);
        assertTrue(result);
    }

    @Test
    void testFindUser_ValidInput_ReturnsUser() {
        Random random = new Random();
        int id = random.nextInt();
        User expectedUser = new User();
        when(userRepository.findById(id)).thenReturn(Optional.of(expectedUser));
        User result = userGateway.findById(id);
        assertEquals(expectedUser, result);
    }

    @Test
    void testListAllUsers_ReturnsListOfUsers() {
        List<User> expectedUsers = List.of(new User(), new User());
        when(userRepository.findAll()).thenReturn(expectedUsers);
        List<User> result = userGateway.listAllUsers();
        assertEquals(expectedUsers, result);
    }
}
