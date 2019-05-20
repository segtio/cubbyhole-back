package com.kata.cubbyhole.web.rest;

import com.kata.cubbyhole.config.logging.annotation.MethodLogger;
import com.kata.cubbyhole.model.User;
import com.kata.cubbyhole.repository.UserRepository;
import com.kata.cubbyhole.service.UserService;
import com.kata.cubbyhole.web.annotation.V1APIController;
import com.kata.cubbyhole.web.payload.JwtAuthenticationResponse;
import com.kata.cubbyhole.web.payload.LoginRequest;
import com.kata.cubbyhole.web.payload.SignUpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@RestController
@V1APIController
public class AuthController extends AbstractRestController {


    private final String RESOURCE_PREFIX = "/auth";

    private final UserService userService;

    private final UserRepository userRepository;

    @Autowired
    public AuthController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping(RESOURCE_PREFIX + "/login")
    @MethodLogger
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = this.userService.authenticate(
                loginRequest.getUsernameOrEmail(),
                loginRequest.getPassword()
        );
        String token = this.userService.generateToken(authentication);
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

        User user = this.userService.register(
                signUpRequest.getName(),
                signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                signUpRequest.getPassword());


        return buildApiResponse(true, String.format("User %s registered successfully!", user.getUsername()), OK);
    }

}
