package com.postech.msuser.dto;

import com.postech.msuser.entity.User;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    Integer id;
    @NotNull
    private String username;
    @NotNull
    private String email;
    @NotNull
    private String login;
    @NotNull
    private String password;
    @NotNull
    private String passwordConfirmation;
    @NotNull
    private String role;

    public User toEntity() {
        return new User(this);
    }
}