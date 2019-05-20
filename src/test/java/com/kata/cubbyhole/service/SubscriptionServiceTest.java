package com.kata.cubbyhole.service;

import com.kata.cubbyhole.model.Plan;
import com.kata.cubbyhole.model.Subscription;
import com.kata.cubbyhole.model.User;
import com.kata.cubbyhole.repository.PlanRepository;
import com.kata.cubbyhole.repository.SubscriptionRepository;
import com.kata.cubbyhole.repository.UserRepository;
import com.kata.cubbyhole.runner.SpringJUnitParams;
import com.kata.cubbyhole.service.impl.SubscriptionServiceImpl;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnitParams.class)
@SpringBootTest
@Transactional
public class SubscriptionServiceTest {

    private SubscriptionService subscriptionService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Before
    public void setup() {
        this.subscriptionService = new SubscriptionServiceImpl(subscriptionRepository);
    }


    @Test
    @Sql({"/data/users.sql"})
    @Parameters({"Cervantes"})
    public void should_save_subscription(String username) {
        User user = userRepository.findByUsername(username).get();
        Plan plan = planRepository.findById(1L).get();
        Instant expireOn = Instant.now().plus(plan.getDuration(), DAYS);
        Subscription subscription = subscriptionService.save(user, plan);
        assertThat(subscription).isNotNull();
        assertThat(subscription.getExpireOn()).isEqualTo(expireOn);
    }
}
