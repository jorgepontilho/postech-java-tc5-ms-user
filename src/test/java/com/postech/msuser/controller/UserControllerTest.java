package com.postech.msuser.controller;

import com.postech.msuser.dto.UserDTO;
import com.postech.msuser.entity.User;
import com.postech.msuser.gateway.UserGateway;
import com.postech.msuser.request.UserAuthRequest;
import com.postech.msuser.security.SecurityFilter;
import com.postech.msuser.security.TokenService;
import com.postech.msuser.util.UserUtilTest;
import jakarta.servlet.http.HttpServletRequest;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserControllerTest {

    @Mock
    private UserGateway userGateway;
    @InjectMocks
    private UserController userController;
    @Mock
    private TokenService tokenService;
    @Mock
    private SecurityFilter securityFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    class CreatetUser {
        @Test
        void devePermitirRegistrarUsuario() {
            HttpServletRequest request = mock(HttpServletRequest.class);
            UserDTO userDTO = UserUtilTest.createUserDTO();
            when(userGateway.createUser(any())).thenReturn(userDTO);
            ResponseEntity<?> response = userController.createUser(request, userDTO);
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
        }

        @Test
        void deveGerarExcecaoQuandoRegistrarUsuarioNomeNulo() {
            HttpServletRequest request = mock(HttpServletRequest.class);
            UserDTO userDTO = new UserDTO();
            ResponseEntity<?> response = userController.createUser(request, userDTO);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }
    
    @Nested
    class LoginUser {
        @Test
        void devePermitirLogarUsuario() {
            UserAuthRequest userAuth = UserUtilTest.createUserAuthRequest();
            UserDTO userDTO = UserUtilTest.createUserDTO();
            when(userGateway.findByLoginAndPassword(any(), any())).thenReturn(userDTO);
            when(tokenService.generateToken(any())).thenReturn("123456789abcd");
            userController.setTokenService(tokenService);
            ResponseEntity<?> response = userController.login(userAuth);
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        @Test
        void deveValidarToken() {
            User user = UserUtilTest.createUser();
            when(securityFilter.validToken(any())).thenReturn(user);
            userController.setSecurityFilter(securityFilter);
            ResponseEntity<?> response = userController.validToken("123456789abcd");
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }
    }


    @Nested
    class ReadUser {
        @Test
        void devePermitirPesquisarUmUsuario() {
            HttpServletRequest request = mock(HttpServletRequest.class);
            int id = 123;
            UserDTO user = new UserDTO();
            when(userGateway.findById(id)).thenReturn(user);
            ResponseEntity<?> response = userController.findUser(request, id);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(user, response.getBody());
        }

        @Test
        void devePermitirListarTodosUsuarios() {
            HttpServletRequest request = mock(HttpServletRequest.class);
            List<UserDTO> users = new ArrayList<>();
            users.add(new UserDTO());
            users.add(new UserDTO());

            when(userGateway.listAll()).thenReturn(users);
            ResponseEntity<List<UserDTO>> response = (ResponseEntity<List<UserDTO>>) userController.listAllUsers(request);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(users, response.getBody());
        }

        @Test
        void deveGerarExcecaoSeNaoEncontrarUsuario() {
            HttpServletRequest request = mock(HttpServletRequest.class);
            int invalidId = 999;
            when(userGateway.findById(invalidId)).thenReturn(null);
            ResponseEntity<?> response = userController.findUser(request, invalidId);
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertEquals("Usuário não encontrado.", response.getBody());
        }
    }

    @Nested
    class UpdateUser {
        @Test
        void devePermitirAtualizarUsuario() {
            HttpServletRequest request = mock(HttpServletRequest.class);
            UserDTO userNew = UserUtilTest.createUserDTO();
            when(userGateway.findById(userNew.getId())).thenReturn(userNew);
            when(userGateway.updateUser(userNew)).thenReturn(userNew);

            ResponseEntity<?> response = userController.updateUser(request, userNew.getId(), UserUtilTest.createUserDTO());
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        @Test
        void deveGerarExcecaoQuandoAtualizarUsuarioNãoEncontrado() {
            HttpServletRequest request = mock(HttpServletRequest.class);
            int invalidId = 999;
            UserDTO userDTO = new UserDTO();
            ResponseEntity<?> response = userController.updateUser(request, invalidId, userDTO);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }

    @Nested
    class DeleteUser {
        @Test
        void devePermitirApagarUsuario() {
            HttpServletRequest request = mock(HttpServletRequest.class);
            UserDTO user = UserUtilTest.createUserDTO();
            ;
            when(userGateway.findById(user.getId())).thenReturn(user);
            ResponseEntity<?> response = userController.deleteUser(request, user.getId());
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("Usuário removido.", response.getBody());
        }

        @Test
        void deveGerarExcecaoQuandoDeletarUsuarioNãoEncontrado() {
            HttpServletRequest request = mock(HttpServletRequest.class);
            int id = 999;
            when(userGateway.findById(id)).thenReturn(null);
            ResponseEntity<?> response = userController.deleteUser(request, id);
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertEquals("Usuário não encontrado.", response.getBody());
        }
    }
}

