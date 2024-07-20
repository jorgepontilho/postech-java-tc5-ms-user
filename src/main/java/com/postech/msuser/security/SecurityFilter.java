package com.postech.msuser.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.postech.msuser.entity.User;
import com.postech.msuser.gateway.UserGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.postech.msuser.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityFilter extends OncePerRequestFilter {
    @Autowired
    TokenService tokenService;
    @Autowired
    UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = this.recoverToken(request);
        validateRequest(request);
        filterChain.doFilter(request, response);
    }

    public User validToken(String token) {
        try {
            String login = tokenService.validateToken(token);
            User user = userRepository.findByLogin(login);
            Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return user;
        } catch (Exception e) {
            return null;
        }
    }

    private void validateRequest(HttpServletRequest request) {
        var token = this.recoverToken(request);
        if (token == null) {
            request.setAttribute("error_code", HttpStatus.BAD_REQUEST);
            request.setAttribute("error", "Bearer token inválido");
            return;
        }

        SecurityUser securityUser = UserGateway.getUserFromToken(token);
        if (securityUser == null) {
            request.setAttribute("error_code", HttpStatus.BAD_REQUEST);
            request.setAttribute("error", "Bearer token inválido");
            return;
        }

        if (!checkAuthorization(request.getMethod(), securityUser.getRole())) {
            request.setAttribute("error_code", HttpStatus.METHOD_NOT_ALLOWED);
            request.setAttribute("error", "Método [" + request.getMethod()
                    + "] não autorizado [" + securityUser + "]");
        }
    }

    private boolean checkAuthorization(String method, String securityEnumUserRole) {
        List<SecurityMethodAuthorized> methodAuthLst = new ArrayList<>();
        methodAuthLst.add(new SecurityMethodAuthorized("GET", "USER"));
        methodAuthLst.add(new SecurityMethodAuthorized("GET", "ADMIN"));
        methodAuthLst.add(new SecurityMethodAuthorized("POST", "ADMIN"));
        methodAuthLst.add(new SecurityMethodAuthorized("PUT", "ADMIN"));
        methodAuthLst.add(new SecurityMethodAuthorized("DELETE", "ADMIN"));

        for (SecurityMethodAuthorized methodAuth : methodAuthLst) {
            if (methodAuth.getMethod().equals(method)
                    && methodAuth.getRole().equals(securityEnumUserRole)) {
                return true;
            }
        }
        return false;
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }
}