package com.kata.cubbyhole.web.rest;

import com.kata.cubbyhole.config.logging.annotation.MethodLogger;
import com.kata.cubbyhole.model.Role;
import com.kata.cubbyhole.model.User;
import com.kata.cubbyhole.repository.RoleRepository;
import com.kata.cubbyhole.repository.UserRepository;
import com.kata.cubbyhole.security.jwt.JwtTokenProvider;
import com.kata.cubbyhole.web.annotation.V1APIController;
import com.kata.cubbyhole.web.exception.InternalException;
import com.kata.cubbyhole.web.payload.JwtAuthenticationResponse;
import com.kata.cubbyhole.web.payload.LoginRequest;
import com.kata.cubbyhole.web.payload.SignUpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collections;

import static com.kata.cubbyhole.model.enumeration.RoleName.ROLE_USER;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@RestController
@V1APIController
public class AuthController extends AbstractRestController {


    private final String RESOURCE_PREFIX = "/auth";

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider tokenProvider;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping(RESOURCE_PREFIX + "/login")
    @MethodLogger
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = doAuthentication(loginRequest);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(token));
    }

    @PostMapping(RESOURCE_PREFIX + "/register")
    @MethodLogger
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {

            return buildApiResponse(false, "Username is already taken!", BAD_REQUEST);
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return buildApiResponse(false, "Email Address already in use!", BAD_REQUEST);
        }

        User user = buildUserFromSignUpRequest(signUpRequest);


        User result = userRepository.save(user);

        return buildApiResponse(true, String.format("User %s registered successfully!", result.getUsername()), OK);
    }

    private Authentication doAuthentication(@Valid LoginRequest loginRequest) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()));
    }

    private String generateToken(Authentication authentication) {
        return tokenProvider.generateToken(authentication);
    }

    private User buildUserFromSignUpRequest(@Valid SignUpRequest signUpRequest) {
        User user = new User(signUpRequest.getName(), signUpRequest.getUsername(),
                signUpRequest.getEmail(), signUpRequest.getPassword());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role userRole = defaultUserRole();
        user.setRoles(Collections.singleton(userRole));
        return user;
    }

    private Role defaultUserRole() {
        return roleRepository.findByName(ROLE_USER)
                .orElseThrow(() -> new InternalException("User Role not set."));
    }

}
