package com.postech.msuser.interfaces;

import com.postech.msuser.entity.User;

import java.util.List;

public interface IUserGateway {
    public User createUser(User user);

    public User updateUser(User user);

    public boolean deleteUser(Integer id);

    public User findById(Integer id);

    User findByLoginAndPassword(String login, String password);

    public User findByLogin(String login);

    public List<User> listAllUsers();
}
