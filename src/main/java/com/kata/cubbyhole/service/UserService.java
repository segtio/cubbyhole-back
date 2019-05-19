package com.kata.cubbyhole.service;

import org.springframework.security.core.Authentication;

public interface UserService {

    Authentication authenticate(String usernameOrEmail, String password);

    String generateToken(Authentication authentication);
}
