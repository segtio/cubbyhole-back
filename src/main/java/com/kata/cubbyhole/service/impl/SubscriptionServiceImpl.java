package com.kata.cubbyhole.service.impl;

import com.kata.cubbyhole.model.Plan;
import com.kata.cubbyhole.model.Subscription;
import com.kata.cubbyhole.model.User;
import com.kata.cubbyhole.repository.SubscriptionRepository;
import com.kata.cubbyhole.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
@Transactional
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    @Autowired
    public SubscriptionServiceImpl(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public Subscription save(User user, Plan plan) {
        Instant expireOn = Instant.now().plus(plan.getDuration(), DAYS);
        Subscription subscription = new Subscription(expireOn, user, plan);
        return this.subscriptionRepository.save(subscription);
    }
}
