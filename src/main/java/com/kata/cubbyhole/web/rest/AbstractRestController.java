package com.kata.cubbyhole.web.rest;

import com.kata.cubbyhole.web.payload.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


public abstract class AbstractRestController {
    protected ResponseEntity<?> buildApiResponse(boolean success, String message, HttpStatus code) {
        return new ResponseEntity<>(new ApiResponse(success, message), code);
    }
}
