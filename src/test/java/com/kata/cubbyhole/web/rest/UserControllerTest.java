package com.kata.cubbyhole.web.rest;

import com.kata.cubbyhole.config.Constants;
import com.kata.cubbyhole.runner.SpringJUnitParams;
import com.kata.cubbyhole.utils.TestUtil;
import com.kata.cubbyhole.web.exception.ErrorHandlerAdvice;
import com.kata.cubbyhole.web.payload.SubscriptionRequest;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnitParams.class)
@SpringBootTest
public class UserControllerTest {

    private final String RESOURCE_PREFIX = Constants.APIV1_PREFIX + "/user";

    @InjectMocks
    private UserController userController;

    private MockMvc restMvc;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        userController = new UserController();
        this.restMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setControllerAdvice(new ErrorHandlerAdvice())
                .alwaysDo(MockMvcResultHandlers.print())
                .build();
    }

    @Test
    @Parameters({"1, 1, Free"})
    public void should_return_success_message_when_subscribe_user_to_a_plan(Long userId, Long planId, String planName) throws Exception {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequest(userId, planId);

        restMvc.perform(post(RESOURCE_PREFIX + "/subscribe")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(subscriptionRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string(String.format("{\"success\":true,\"message\":\"User registered successfully to %s!\"}", planName)));
    }

    @Test
    @Parameters({"1, 7"})
    public void should_return_500_code_when_plan_is_not_saved(Long userId, Long planId) throws Exception {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequest(userId, planId);

        restMvc.perform(post(RESOURCE_PREFIX + "/register")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(subscriptionRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(String.format("{\"message\":\"Plan not set.\",\"errorCode\":\"%s\"}", INTERNAL_SERVER_ERROR.getReasonPhrase())));
    }

}
