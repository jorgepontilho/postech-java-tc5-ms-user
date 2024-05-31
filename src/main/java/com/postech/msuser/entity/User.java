package com.postech.msuser.entity;

import com.postech.msuser.dto.UserDTO;
import com.postech.msuser.entity.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Random;

@Data
@Entity
@Table(name = "tb_User")
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String username;
    private String email;
    private String login;
    private String password;
    private String passwordConfirmation;
    private UserRole role;

    public User(UserDTO UserDTO) {
        if (UserDTO.getId() == 0) {
            Random random = new Random();
            this.id = random.nextInt();
        } else {
            this.id = UserDTO.getId();
        }
        this.username = UserDTO.getUsername();
        this.email = UserDTO.getEmail();
        this.login = UserDTO.getLogin();
        this.password = UserDTO.getPassword();
        this.passwordConfirmation = UserDTO.getPasswordConfirmation();
        setRole(UserDTO.getRole());
    }

    public UserDTO toDTO() {
        return new UserDTO(
                this.id,
                this.username,
                this.email,
                this.login,
                this.password,
                this.passwordConfirmation,
                this.role.toString()
        );
    }
    public void setRole(String role) {
          for (UserRole rule : UserRole.values()) {
            if (role.equals(rule.toString())) {
                this.role = rule;
            }
        }

    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role == UserRole.ADMIN)
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
        else
            return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
