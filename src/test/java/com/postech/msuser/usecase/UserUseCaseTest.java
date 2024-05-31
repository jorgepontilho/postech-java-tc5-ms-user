package com.postech.msuser.usecase;

import com.postech.msuser.entity.User;

import static org.junit.jupiter.api.Assertions.*;

import com.postech.msuser.util.UserUtilTest;
import org.junit.jupiter.api.Test;

public class UserUseCaseTest {
    @Test
    void testValidarUsuario_OK() {
        User user = UserUtilTest.createUser();
        assertDoesNotThrow(() -> UserUseCase.validarUsuario(user.toDTO()));
    }

    @Test
    void testValidarUsuario_Null() {
        assertThrows(IllegalArgumentException.class, () -> UserUseCase.validarUsuario(null));
    }

    @Test
    void testValidarUsuario_Senha_Invalida() {
        User user = UserUtilTest.createUser();
        user.setPassword("");
        assertThrows(IllegalArgumentException.class, () -> UserUseCase.validarUsuario(user.toDTO()));
    }

    @Test
    void testValidarDeleteUsuario_ok() {
        User user = UserUtilTest.createUser();
        assertDoesNotThrow(() -> UserUseCase.validarDeleteUsuario(user.toDTO()));
    }

    @Test
    void testValidarDeleteUser_Null() {
        assertThrows(IllegalArgumentException.class, () -> UserUseCase.validarDeleteUsuario(null));
    }
}
