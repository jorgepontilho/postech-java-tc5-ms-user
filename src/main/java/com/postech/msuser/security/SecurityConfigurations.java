package com.postech.msuser.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfigurations {

    @Autowired
    SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizeConfig -> {
                    authorizeConfig.requestMatchers(
                                    "/swagger-ui/**", "/swagger-resources/**",
                                    "/v3/api-docs", "/v3/api-docs/**", "/webjars/**", "")
                            .permitAll();

                    authorizeConfig.requestMatchers(HttpMethod.POST, "/users/login")
                            .permitAll();

                    authorizeConfig.requestMatchers(HttpMethod.GET, "/users/token", "/users/token/**")
                            .permitAll();

                    authorizeConfig.requestMatchers("/users", "/users/**")
                            .permitAll().anyRequest().authenticated();

                    /**************************
                     authorizeConfig.anyRequest().authenticated();

                     authorizeConfig.requestMatchers(
                     "/users").hasRole("ADMIN")
                     .anyRequest()
                     .authenticated();

                     authorizeConfig.requestMatchers(
                     "/users", "/users/**")
                     .permitAll()
                     .anyRequest()
                     .authenticated();
                     *******************************************/
                }).addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
