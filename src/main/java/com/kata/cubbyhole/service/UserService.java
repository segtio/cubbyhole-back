package com.kata.cubbyhole.service;

import com.kata.cubbyhole.model.User;
import org.springframework.security.core.Authentication;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public interface UserService {

    Authentication authenticate(@NotBlank String usernameOrEmail, @NotBlank String password);

    String generateToken(Authentication authentication);

    User register(@NotBlank String name, @NotBlank String usename, @NotBlank @Email String email, @NotBlank String password);
}
