package com.kata.cubbyhole.web.rest;

import com.kata.cubbyhole.config.Constants;
import com.kata.cubbyhole.config.logging.annotation.MethodLogger;
import com.kata.cubbyhole.model.Plan;
import com.kata.cubbyhole.model.Subscription;
import com.kata.cubbyhole.model.User;
import com.kata.cubbyhole.repository.PlanRepository;
import com.kata.cubbyhole.repository.UserRepository;
import com.kata.cubbyhole.service.SubscriptionService;
import com.kata.cubbyhole.web.exception.InternalException;
import com.kata.cubbyhole.web.payload.ApiResponse;
import com.kata.cubbyhole.web.payload.SubscriptionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(Constants.APIV1_PREFIX + "/user")
public class UserController {

    private final SubscriptionService subscriptionService;

    private final UserRepository userRepository;

    private final PlanRepository planRepository;

    @Autowired
    public UserController(SubscriptionService subscriptionService, UserRepository userRepository, PlanRepository planRepository) {
        this.subscriptionService = subscriptionService;
        this.userRepository = userRepository;
        this.planRepository = planRepository;
    }

    @PostMapping("/subscribe")
    @MethodLogger
    public ApiResponse subscribeUser(@Valid @RequestBody SubscriptionRequest subscriptionRequest) {
        User user = userRepository.findById(subscriptionRequest.getUserId())
                .orElseThrow(() -> new InternalException("User Not found !"));
        Plan plan = planRepository.findById(subscriptionRequest.getPlanId())
                .orElseThrow(() -> new InternalException("Plan Not found !"));

        Subscription subscription = subscriptionService.save(user, plan);
        return new ApiResponse(true, String.format("User registered successfully to %s!", subscription.getPlan().getName()));
    }
}
