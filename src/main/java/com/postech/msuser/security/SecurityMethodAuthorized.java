package com.postech.msuser.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class SecurityMethodAuthorized {
    String method;
    String role;
}