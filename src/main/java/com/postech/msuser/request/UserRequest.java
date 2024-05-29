package com.postech.msuser.request;

import com.postech.msuser.entity.enums.UserRole;
public record UserRequest(String login, String password, UserRole role) {
}
