package com.kata.cubbyhole.service.impl;

import com.kata.cubbyhole.model.Role;
import com.kata.cubbyhole.model.User;
import com.kata.cubbyhole.repository.RoleRepository;
import com.kata.cubbyhole.repository.UserRepository;
import com.kata.cubbyhole.security.jwt.JwtTokenProvider;
import com.kata.cubbyhole.service.UserService;
import com.kata.cubbyhole.web.exception.InternalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static com.kata.cubbyhole.model.enumeration.RoleName.ROLE_USER;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider, PasswordEncoder passwordEncoder, RoleRepository roleRepository, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Authentication authenticate(String usernameOrEmail, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(usernameOrEmail, password)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

    @Override
    public String generateToken(Authentication authentication) {
        return tokenProvider.generateToken(authentication);
    }

    @Override
    public User register(String name, String username, String email, String password) {
        User user = new User(name, username, email, password);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role userRole = defaultUserRole();
        user.setRoles(Collections.singleton(userRole));
        return userRepository.save(user);
    }

    private Role defaultUserRole() {
        return roleRepository.findByName(ROLE_USER)
                .orElseThrow(() -> new InternalException("User Role not set."));
    }
}
