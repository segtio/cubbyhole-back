package com.kata.cubbyhole.web.rest;

import com.kata.cubbyhole.runner.SpringJUnitParams;
import com.kata.cubbyhole.security.jwt.JwtTokenProvider;
import com.kata.cubbyhole.utils.TestUtil;
import com.kata.cubbyhole.web.payload.LoginRequest;
import com.kata.cubbyhole.web.payload.SignUpRequest;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnitParams.class)
@SpringBootTest
public class AuthControllerTest {

    private final String RESOURCE_PREFIX = "/api/v1/auth";
    @Autowired
    @Spy
    private JwtTokenProvider jwtTokenProvider;
    private MockMvc restMvc;

    @Before
    public void setup() {
        AuthController authController = new AuthController();
        this.restMvc = MockMvcBuilders
                .standaloneSetup(authController)
                .build();
    }

    @Test
    @Parameters({"Mckay, Hopkins, thetoken"})
    public void should_login_user_and_return_token(String usernameOrEmail, String password, String token) throws Exception {
        LoginRequest user = new LoginRequest(usernameOrEmail, password);

        Mockito.doReturn(token).when(jwtTokenProvider).generateToken(Mockito.any(Authentication.class));

        restMvc.perform(post(RESOURCE_PREFIX + "/login")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(user)))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"accessToken\":\"" + token + "\",\"tokenType\":\"Bearer\"}"));
    }

    @Test
    @Parameters({""})
    public void should_400_when_usernameOrEmail_or_password_empty(String usernameOrEmail, String password, String token) throws Exception {
        LoginRequest user = new LoginRequest(usernameOrEmail, password);

        Mockito.doReturn(token).when(jwtTokenProvider).generateToken(Mockito.any(Authentication.class));

        restMvc.perform(post(RESOURCE_PREFIX + "/login")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(user)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(""));
    }

    @Test
    @Parameters({"Corinne Conway, Mckay, corinnemckay@zidox.com, Hopkins"})
    public void should_register_user_and_return_success_message(String name, String username, String email, String password) throws Exception {
        SignUpRequest user = new SignUpRequest(name, username, email, password);

        restMvc.perform(post(RESOURCE_PREFIX + "/register")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(user)))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"success\":\"true\",\"message\":\"User registered successfully\"}"));
    }

    @Test
    @Parameters({""})
    public void should_400_when_name_or_username_or_email_or_password_empty(String name, String username, String email, String password) throws Exception {
        SignUpRequest user = new SignUpRequest(name, username, email, password);

        restMvc.perform(post(RESOURCE_PREFIX + "/register")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(user)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(""));
    }

    @Test
    @Parameters({"Corinne Conway, Mckay, corinnemckay@zidox.com, Hopkins, Username"})
    public void should_400_when_username_or_email_exists_and_return_error_message(String name, String username, String email, String password, String exists) throws Exception {
        SignUpRequest user = new SignUpRequest(name, username, email, password);

        restMvc.perform(post(RESOURCE_PREFIX + "/register")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(user)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("{\"success\":\"false\",\"message\":" + exists + "\" is already taken!\"}"));
    }

}
