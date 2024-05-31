package com.postech.msuser.util;

import com.postech.msuser.dto.UserDTO;
import com.postech.msuser.entity.User;
import com.postech.msuser.entity.enums.UserRole;

public class UserUtilTest {

    public static User createUser() {
        return new User(createUserDTO());
    }

    public static UserDTO createUserDTO() {
        return new UserDTO(
                1000,
                "John Doe",
                "john.doe@mail.com",
                "jonh",
                "123456",
                "123456",
                UserRole.ADMIN.toString()
        );
    }
}
