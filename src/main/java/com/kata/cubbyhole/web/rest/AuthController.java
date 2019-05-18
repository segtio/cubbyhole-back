package com.kata.cubbyhole.web.rest;

import com.kata.cubbyhole.web.annotation.V1APIController;
import com.kata.cubbyhole.web.payload.LoginRequest;
import com.kata.cubbyhole.web.payload.SignUpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@V1APIController
public class AuthController {

    private final String RESOURCE_PREFIX = "/auth";

    @PostMapping(RESOURCE_PREFIX + "/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return null;
    }

    @PostMapping(RESOURCE_PREFIX + "/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        return null;
    }
}
