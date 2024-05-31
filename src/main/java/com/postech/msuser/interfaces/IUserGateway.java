package com.postech.msuser.interfaces;
import com.postech.msuser.dto.UserDTO;
import java.util.List;

public interface IUserGateway {
    public UserDTO createUser(UserDTO user);

    public UserDTO updateUser(UserDTO user);

    public boolean deleteUser(Integer id);

    public UserDTO findById(Integer id);

    public List<UserDTO> listAll();

    public UserDTO findByLoginAndPassword(String login, String password);

    public UserDTO findByLogin(String login);
}
