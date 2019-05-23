package com.kata.cubbyhole.service;

import com.kata.cubbyhole.model.Plan;
import com.kata.cubbyhole.model.Subscription;
import com.kata.cubbyhole.model.User;

import javax.validation.constraints.NotBlank;

public interface SubscriptionService {

    Subscription save(@NotBlank User user, @NotBlank Plan plan);
}
