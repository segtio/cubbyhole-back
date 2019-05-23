package com.kata.cubbyhole.web.rest;

import com.kata.cubbyhole.config.Constants;
import com.kata.cubbyhole.model.User;
import com.kata.cubbyhole.repository.UserRepository;
import com.kata.cubbyhole.runner.SpringJUnitParams;
import com.kata.cubbyhole.service.UserService;
import com.kata.cubbyhole.utils.TestUtil;
import com.kata.cubbyhole.web.exception.ErrorHandlerAdvice;
import com.kata.cubbyhole.web.exception.InternalException;
import com.kata.cubbyhole.web.payload.LoginRequest;
import com.kata.cubbyhole.web.payload.SignUpRequest;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnitParams.class)
@SpringBootTest
public class AuthControllerTest {

    private final String RESOURCE_PREFIX = Constants.APIV1_PREFIX + "/auth";

    @Autowired
    @Mock
    private UserService userService;

    @Autowired
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthController authController;

    private MockMvc restMvc;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        authController = new AuthController(userService, userRepository);
        this.restMvc = MockMvcBuilders
                .standaloneSetup(authController)
                .setControllerAdvice(new ErrorHandlerAdvice())
                .alwaysDo(MockMvcResultHandlers.print())
                .build();
    }

    @Test
    @Parameters({"Mckay, Hopkins, thetoken"})
    public void should_return_token_when_user_logged_in(String usernameOrEmail, String password, String token) throws Exception {
        LoginRequest user = new LoginRequest(usernameOrEmail, password);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                usernameOrEmail,
                password);

        Mockito.doReturn(usernamePasswordAuthenticationToken).when(userService).authenticate(usernameOrEmail, password);
        Mockito.doReturn(token).when(userService).generateToken(Mockito.any(Authentication.class));

        restMvc.perform(post(RESOURCE_PREFIX + "/login")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(user)))
                .andExpect(status().isOk())
                .andExpect(content().string(String.format("{\"accessToken\":\"%s\",\"tokenType\":\"Bearer\"}", token)));
    }

    @Test
    public void should_return_400_code_when_usernameOrEmail_empty() throws Exception {
        LoginRequest user = new LoginRequest(null, "azerty");
        restMvc.perform(post(RESOURCE_PREFIX + "/login")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(user)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(String.format("{\"message\":\"usernameOrEmail cannot be empty \",\"errorCode\":\"%s\"}", BAD_REQUEST.getReasonPhrase())));
    }

    @Test
    public void should_return_400_code_when_password_empty() throws Exception {
        LoginRequest user = new LoginRequest("azerty", null);
        restMvc.perform(post(RESOURCE_PREFIX + "/login")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(user)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(String.format("{\"message\":\"Password cannot be empty \",\"errorCode\":\"%s\"}", BAD_REQUEST.getReasonPhrase())));
    }

    @Test
    @Parameters({"Corinne Conway, Mckay, corinnemckay@zidox.com, Hopkins"})
    public void should_return_message_sucess_when_user_register(String name, String username, String email, String password) throws Exception {
        SignUpRequest user = new SignUpRequest(name, username, email, password);

        Mockito.doReturn(new User(name, username, email, password)).when(userService).register(name, username, email, password);

        restMvc.perform(post(RESOURCE_PREFIX + "/register")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(user)))
                .andExpect(status().isOk())
                .andExpect(content().string(String.format("{\"success\":true,\"message\":\"User %s registered successfully!\"}", username)));
    }

    @Test
    @Parameters({"Corinne Conway, Mckay, corinnemckay@zidox.com, Hopkins"})
    public void should_return_500_code_when_default_role_is_not_saved(String name, String username, String email, String password) throws Exception {
        SignUpRequest user = new SignUpRequest(name, username, email, password);

        Mockito.doThrow(new InternalException("User Role not set.")).when(userService).register(name, username, email, password);

        restMvc.perform(post(RESOURCE_PREFIX + "/register")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(user)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(String.format("{\"message\":\"User Role not set.\",\"errorCode\":\"%s\"}", INTERNAL_SERVER_ERROR.getReasonPhrase())));
    }

    @Test
    public void should_return_400_code_when_name_not_valid() throws Exception {
        SignUpRequest user = new SignUpRequest(null, "azerty", "corinnemckay@zidox.com", "azerty");

        restMvc.perform(post(RESOURCE_PREFIX + "/register")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(user)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(String.format("{\"message\":\"Name cannot be empty \",\"errorCode\":\"%s\"}", BAD_REQUEST.getReasonPhrase())));
    }

    @Test
    public void should_return_400_code_when_username_not_valid() throws Exception {
        SignUpRequest user = new SignUpRequest("azerty", null, "corinnemckay@zidox.com", "azerty");

        restMvc.perform(post(RESOURCE_PREFIX + "/register")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(user)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(String.format("{\"message\":\"Username cannot be empty \",\"errorCode\":\"%s\"}", BAD_REQUEST.getReasonPhrase())));
    }

    @Test
    public void should_return_400_code_when_email_not_valid() throws Exception {
        SignUpRequest user = new SignUpRequest("azerty", "azerty", null, "azerty");

        restMvc.perform(post(RESOURCE_PREFIX + "/register")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(user)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(String.format("{\"message\":\"Email cannot be empty \",\"errorCode\":\"%s\"}", BAD_REQUEST.getReasonPhrase())));
    }

    @Test
    public void should_return_400_code_when_password_not_valid() throws Exception {
        SignUpRequest user = new SignUpRequest("azerty", "azerty", "corinnemckay@zidox.com", null);

        restMvc.perform(post(RESOURCE_PREFIX + "/register")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(user)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(String.format("{\"message\":\"Password cannot be empty \",\"errorCode\":\"%s\"}", BAD_REQUEST.getReasonPhrase())));
    }

    @Test
    @Parameters({"Corinne Conway, Mckay, corinnemckay@zidox.com, Hopkins"})
    public void should_return_400_code_with_error_message_when_username_exists(String name, String username, String email, String password) throws Exception {
        SignUpRequest user = new SignUpRequest(name, username, email, password);
        Mockito.doReturn(true).when(userRepository).existsByUsername(username);
        restMvc.perform(post(RESOURCE_PREFIX + "/register")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(user)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("{\"success\":false,\"message\":\"Username is already taken!\"}"));
    }

    @Test
    @Parameters({"Corinne Conway, Mckay, corinnemckay@zidox.com, Hopkins"})
    public void should_return_400_code_with_error_message_when_email_exists(String name, String username, String email, String password) throws Exception {
        SignUpRequest user = new SignUpRequest(name, username, email, password);
        Mockito.doReturn(true).when(userRepository).existsByEmail(email);
        restMvc.perform(post(RESOURCE_PREFIX + "/register")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(user)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("{\"success\":false,\"message\":\"Email Address already in use!\"}"));
    }

}
