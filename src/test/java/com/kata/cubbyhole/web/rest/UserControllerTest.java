package com.kata.cubbyhole.web.rest;

import com.kata.cubbyhole.config.Constants;
import com.kata.cubbyhole.repository.PlanRepository;
import com.kata.cubbyhole.repository.UserRepository;
import com.kata.cubbyhole.runner.SpringJUnitParams;
import com.kata.cubbyhole.service.SubscriptionService;
import com.kata.cubbyhole.utils.TestUtil;
import com.kata.cubbyhole.web.exception.ErrorHandlerAdvice;
import com.kata.cubbyhole.web.payload.SubscriptionRequest;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.context.jdbc.SqlConfig.TransactionMode.INFERRED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnitParams.class)
@SpringBootTest
@Transactional
public class UserControllerTest {

    private final String RESOURCE_PREFIX = Constants.APIV1_PREFIX + "/user";

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    // @Mock
    private UserRepository userRepository;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private UserController userController;

    private MockMvc restMvc;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        userController = new UserController(subscriptionService, userRepository, planRepository);
        this.restMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setControllerAdvice(new ErrorHandlerAdvice())
                .alwaysDo(MockMvcResultHandlers.print())
                .build();
    }

    @Test
    @SqlGroup({
            @Sql(executionPhase = BEFORE_TEST_METHOD, scripts = "/data/users.sql", config = @SqlConfig(transactionMode = INFERRED)),
            @Sql(executionPhase = AFTER_TEST_METHOD, scripts = "/data/cleanup_users.sql", config = @SqlConfig(transactionMode = INFERRED))
    })
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
    @SqlGroup({
            @Sql(executionPhase = BEFORE_TEST_METHOD, scripts = "/data/users.sql", config = @SqlConfig(transactionMode = INFERRED)),
            @Sql(executionPhase = AFTER_TEST_METHOD, scripts = "/data/cleanup_users.sql", config = @SqlConfig(transactionMode = INFERRED))
    })
    @Parameters({"1, 7"})
    public void should_return_500_code_when_plan_is_not_saved(Long userId, Long planId) throws Exception {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequest(userId, planId);

        restMvc.perform(post(RESOURCE_PREFIX + "/subscribe")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(subscriptionRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(String.format("{\"message\":\"Plan Not found !\",\"errorCode\":\"%s\"}", INTERNAL_SERVER_ERROR.getReasonPhrase())));
    }

    @Test
    @Parameters({"1, 7"})
    public void should_return_500_code_when_user_is_not_saved(Long userId, Long planId) throws Exception {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequest(userId, planId);

        restMvc.perform(post(RESOURCE_PREFIX + "/subscribe")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(subscriptionRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(String.format("{\"message\":\"User Not found !\",\"errorCode\":\"%s\"}", INTERNAL_SERVER_ERROR.getReasonPhrase())));
    }

    @Test
    @Parameters({"7"})
    public void should_return_400_code_when_userId_empty(Long planId) throws Exception {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequest(null, planId);

        restMvc.perform(post(RESOURCE_PREFIX + "/subscribe")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(subscriptionRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(String.format("{\"message\":\"user cannot be empty \",\"errorCode\":\"%s\"}", BAD_REQUEST.getReasonPhrase())));
    }

    @Test
    @Parameters({"1"})
    public void should_return_400_code_when_planId_empty(Long userId) throws Exception {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequest(userId, null);

        restMvc.perform(post(RESOURCE_PREFIX + "/subscribe")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(subscriptionRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(String.format("{\"message\":\"plan cannot be empty \",\"errorCode\":\"%s\"}", BAD_REQUEST.getReasonPhrase())));
    }

}
