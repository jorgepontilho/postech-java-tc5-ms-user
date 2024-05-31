package com.postech.msuser.usecase;

import com.postech.msuser.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserUseCase {

    public static void validarUsuario(UserDTO userDTO) {

        if (userDTO == null) {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }
        if (!userDTO.getPassword().equals(userDTO.getPasswordConfirmation())) {
            throw new IllegalArgumentException("A senha e a confirmação devem ser iguais!.");
        }
    }

    public static void validarDeleteUsuario(UserDTO userDTO) {
        if (userDTO == null) {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }
    }
}
