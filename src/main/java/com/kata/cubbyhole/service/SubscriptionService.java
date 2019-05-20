package com.kata.cubbyhole.service;

import com.kata.cubbyhole.model.Plan;
import com.kata.cubbyhole.model.Subscription;
import com.kata.cubbyhole.model.User;

public interface SubscriptionService {

    Subscription save(User user, Plan plan);
}
