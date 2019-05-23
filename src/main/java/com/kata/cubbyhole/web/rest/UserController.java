package com.kata.cubbyhole.web.rest;

import com.kata.cubbyhole.config.Constants;
import com.kata.cubbyhole.config.logging.annotation.MethodLogger;
import com.kata.cubbyhole.web.payload.ApiResponse;
import com.kata.cubbyhole.web.payload.SubscriptionRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(Constants.APIV1_PREFIX + "/user")
public class UserController {

    @PostMapping("/subscribe")
    @MethodLogger
    public ResponseEntity<ApiResponse> subscribeUser(@Valid @RequestBody SubscriptionRequest subscriptionRequest) {
        return null;
    }
}
