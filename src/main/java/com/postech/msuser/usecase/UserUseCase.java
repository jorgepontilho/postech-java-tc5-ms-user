package com.postech.msuser.usecase;

import com.postech.msuser.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserUseCase {

    public static void validarUsuario(User user) {

        if (user == null) {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }
        if (!user.getPassword().equals(user.getPasswordConfirmation())) {
            throw new IllegalArgumentException("A senha e a confirmação devem ser iguais!.");
        }
    }

    public static void validarDeleteUsuario(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }
    }
}
