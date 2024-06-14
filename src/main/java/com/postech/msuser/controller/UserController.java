package com.postech.msuser.controller;

import com.postech.msuser.dto.UserDTO;
import com.postech.msuser.entity.User;
import com.postech.msuser.gateway.UserGateway;
import com.postech.msuser.request.UserAuthRequest;
import com.postech.msuser.security.SecurityFilter;
import com.postech.msuser.usecase.UserUseCase;
import com.postech.msuser.security.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    @Setter
    @Autowired
    private TokenService tokenService;

    @Setter
    @Autowired
    private SecurityFilter securityFilter;

    private final UserGateway userGateway;

    @PostMapping("")
    @Operation(summary = "Create a new user with a DTO", responses = {
            @ApiResponse(description = "The new users was created", responseCode = "201", content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(description = "Fields Invalid", responseCode = "400", content = @Content(schema = @Schema(type = "string", example = "Campos inválidos ou faltando")))
    })
    public ResponseEntity<?> createUser(HttpServletRequest request, @Valid @RequestBody UserDTO userDTO) {
        log.info("PostMapping - createUser for user [{}]", userDTO.getUsername());
        if (request.getAttribute("error") != null) {
            return ResponseEntity.status((HttpStatusCode) request.getAttribute("error_code"))
                    .body(request.getAttribute("error"));
        }
        try {
            UserUseCase.validarUsuario(userDTO);
            if (userGateway.findByLogin(userDTO.getLogin()) != null) {
                return new ResponseEntity<>("Login já existe.", HttpStatus.BAD_REQUEST);
            }
            UserDTO userCreated = userGateway.createUser(userDTO);
            return new ResponseEntity<>(userCreated, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get only user by ID", responses = {
            @ApiResponse(description = "The user by ID", responseCode = "200", content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(description = "User Not Found", responseCode = "404", content = @Content(schema = @Schema(type = "string", example = "Usuário não encontrado.")))
    })
    public ResponseEntity<?> findUser(HttpServletRequest request, @PathVariable Integer id) {
        log.info("GetMapping - findUser");
        if (request.getAttribute("error") != null) {
            return ResponseEntity.status((HttpStatusCode) request.getAttribute("error_code"))
                    .body(request.getAttribute("error"));
        }
        UserDTO userDTO = userGateway.findById(id);
        if (userDTO != null) {
            return new ResponseEntity<>(userDTO, HttpStatus.OK);
        }
        return new ResponseEntity<>("Usuário não encontrado.", HttpStatus.NOT_FOUND);
    }

    @GetMapping("")
    @Operation(summary = "Get all users", responses = {
            @ApiResponse(description = "List of all users", responseCode = "200"),
    })
    public ResponseEntity<?> listAllUsers(HttpServletRequest request) {
        log.info("GetMapping - listAllUsers");
        if (request.getAttribute("error") != null) {
            return ResponseEntity.status((HttpStatusCode) request.getAttribute("error_code"))
                    .body(request.getAttribute("error"));
        }
        return new ResponseEntity<>(userGateway.listAll(), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Request for update a user by ID", responses = {
            @ApiResponse(description = "The users was updated", responseCode = "200", content = @Content(schema = @Schema(implementation = User.class)))
    })
    public ResponseEntity<?> updateUser(HttpServletRequest request, @PathVariable Integer id, @RequestBody @Valid UserDTO userDTO) {
        log.info("PutMapping - updateUser");
        if (request.getAttribute("error") != null) {
            return ResponseEntity.status((HttpStatusCode) request.getAttribute("error_code"))
                    .body(request.getAttribute("error"));
        }
        try {
            UserDTO userOld = userGateway.findById(id);
            UserUseCase.validarUsuario(userOld);

            userDTO.setId(id);
            UserUseCase.validarUsuario(userDTO);

            if (!userDTO.getLogin().equals(userOld.getLogin())) {
                return new ResponseEntity<>("Não é permitido alterar o Login.", HttpStatus.BAD_REQUEST);
            }

            userDTO = userGateway.updateUser(userDTO);
            return new ResponseEntity<>(userDTO, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user by ID", responses = {
            @ApiResponse(description = "The user was deleted", responseCode = "200", content = @Content(schema = @Schema(type = "string", example = "Usuário removido."))),
            @ApiResponse(description = "User Not Found", responseCode = "404", content = @Content(schema = @Schema(type = "string", example = "Usuário não encontrado.")))
    })
    public ResponseEntity<?> deleteUser(HttpServletRequest request, @PathVariable Integer id) {
        log.info("DeleteMapping - deleteUser");
        if (request.getAttribute("error") != null) {
            return ResponseEntity.status((HttpStatusCode) request.getAttribute("error_code"))
                    .body(request.getAttribute("error"));
        }
        try {
            UserDTO userDTO = userGateway.findById(id);
            UserUseCase.validarDeleteUsuario(userDTO);
            userGateway.deleteUser(id);
            return new ResponseEntity<>("Usuário removido.", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Get Token by Login and Password", responses = {
            @ApiResponse(description = "The Token by login", responseCode = "200", content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(description = "User Not Found", responseCode = "404", content = @Content(schema = @Schema(type = "string", example = "Login ou senha inválida.")))
    })
    public ResponseEntity login(@RequestBody @Valid UserAuthRequest data) {
        log.info("PostMapping - Login for user [{}]", data);
        try {
            UserDTO userDTO = userGateway.findByLoginAndPassword(data.login(), data.password());
            if (userDTO == null) {
                return new ResponseEntity<>("Login ou Senha inválida.", HttpStatus.BAD_REQUEST);
            }
            String token = tokenService.generateToken(userDTO);
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/token/{token}")
    @Operation(summary = "Valid Token", responses = {
            @ApiResponse(description = "The user by token", responseCode = "200", content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(description = "User Not Found", responseCode = "404", content = @Content(schema = @Schema(type = "string", example = "Token inválido.")))
    })
    public ResponseEntity<?> validToken(@PathVariable String token) {
        log.info("GetMapping - validToken");
        User user = securityFilter.validToken(token);
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        return new ResponseEntity<>("Token inválido.", HttpStatus.NOT_FOUND);
    }
}

