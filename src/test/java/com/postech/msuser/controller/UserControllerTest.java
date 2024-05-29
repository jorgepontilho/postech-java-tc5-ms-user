package com.postech.msuser.controller;

import com.postech.msuser.dto.UserDTO;
import com.postech.msuser.entity.User;
import com.postech.msuser.gateway.UserGateway;
import com.postech.msuser.util.UserUtilTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class UserControllerTest {

    @Mock
    private UserGateway userGateway;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    class CreatetUser {
        @Test
        void devePermitirRegistrarUsuario() {
            UserDTO userDTO = UserUtilTest.createUserDTO();
            when(userGateway.createUser(any())).thenReturn(new User());
            ResponseEntity<?> response = userController.createUser(userDTO);
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
        }

        @Test
        void deveGerarExcecaoQuandoRegistrarUsuarioNomeNulo() {
            UserDTO userDTO = new UserDTO();
            ResponseEntity<?> response = userController.createUser(userDTO);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }

    @Nested
    class ReadUser {
        @Test
        void devePermitirPesquisarUmUsuario() {
            int id = 123;
            User user = new User();
            when(userGateway.findById(id)).thenReturn(user);
            ResponseEntity<?> response = userController.findUser(id);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(user, response.getBody());
        }

        @Test
        void devePermitirListarTodosUsuarios() {
            List<User> users = new ArrayList<>();
            users.add(new User());
            users.add(new User());

            when(userGateway.listAllUsers()).thenReturn(users);
            ResponseEntity<List<User>> response = userController.listAllUsers();
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(users, response.getBody());
        }

        @Test
        void deveGerarExcecaoSeNaoEncontrarUsuario() {
            int invalidId = 999;
            when(userGateway.findById(invalidId)).thenReturn(null);
            ResponseEntity<?> response = userController.findUser(invalidId);
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertEquals("Usuário não encontrado.", response.getBody());
        }
    }

    @Nested
    class UpdateUser {
        @Test
        void devePermitirAtualizarUsuario() {
            User userNew = UserUtilTest.createUser();
            when(userGateway.findById(userNew.getId())).thenReturn(userNew);
            when(userGateway.updateUser(userNew)).thenReturn(userNew);

            ResponseEntity<?> response = userController.updateUser(userNew.getId(), UserUtilTest.createUserDTO());
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        @Test
        void deveGerarExcecaoQuandoAtualizarUsuarioNãoEncontrado() {
            int invalidId = 999;
            UserDTO userDTO = new UserDTO();
            ResponseEntity<?> response = userController.updateUser(invalidId, userDTO);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }

    @Nested
    class DeleteUser {
        @Test
        void devePermitirApagarUsuario() {
            User user =  UserUtilTest.createUser();;
            when(userGateway.findById(user.getId())).thenReturn(user);
            ResponseEntity<?> response = userController.deleteUser(user.getId());
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("Usuário removido.", response.getBody());
        }

        @Test
        void deveGerarExcecaoQuandoDeletarUsuarioNãoEncontrado() {
            int id = 999;
            when(userGateway.findById(id)).thenReturn(null);
            ResponseEntity<?> response = userController.deleteUser(id);
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertEquals("Usuário não encontrado.", response.getBody());
        }
    }
}

