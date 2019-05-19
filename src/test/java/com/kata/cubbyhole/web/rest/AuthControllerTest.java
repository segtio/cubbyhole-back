package com.kata.cubbyhole.web.rest;

import com.kata.cubbyhole.model.Role;
import com.kata.cubbyhole.model.User;
import com.kata.cubbyhole.model.enumeration.RoleName;
import com.kata.cubbyhole.repository.RoleRepository;
import com.kata.cubbyhole.repository.UserRepository;
import com.kata.cubbyhole.runner.SpringJUnitParams;
import com.kata.cubbyhole.security.jwt.JwtTokenProvider;
import com.kata.cubbyhole.utils.TestUtil;
import com.kata.cubbyhole.web.exception.ErrorHandlerAdvice;
import com.kata.cubbyhole.web.payload.LoginRequest;
import com.kata.cubbyhole.web.payload.SignUpRequest;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
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

    @Autowired
    @Mock
    private AuthenticationManager authenticationManager;

    @Autowired
    @Mock
    private UserRepository userRepository;

    @Autowired
    @Mock
    private RoleRepository roleRepository;

    @Autowired
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthController authController;

    private MockMvc restMvc;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        authController = new AuthController(authenticationManager, jwtTokenProvider, userRepository, roleRepository, passwordEncoder);
        this.restMvc = MockMvcBuilders
                .standaloneSetup(authController)
                .setControllerAdvice(new ErrorHandlerAdvice())
                .alwaysDo(MockMvcResultHandlers.print())
                .build();
    }

    @Test
    @Parameters({"Mckay, Hopkins, thetoken"})
    public void should_login_user_and_return_token(String usernameOrEmail, String password, String token) throws Exception {
        LoginRequest user = new LoginRequest(usernameOrEmail, password);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                usernameOrEmail,
                password);

        Mockito.doReturn(usernamePasswordAuthenticationToken).when(authenticationManager).authenticate(usernamePasswordAuthenticationToken);
        Mockito.doReturn(token).when(jwtTokenProvider).generateToken(Mockito.any(Authentication.class));

        restMvc.perform(post(RESOURCE_PREFIX + "/login")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(user)))
                .andExpect(status().isOk())
                .andExpect(content().string(String.format("{\"accessToken\":\"%s\",\"tokenType\":\"Bearer\"}", token)));
    }

    @Test
    public void should_400_when_usernameOrEmail_or_password_empty() throws Exception {
        LoginRequest user = new LoginRequest(null, "azerty");
        restMvc.perform(post(RESOURCE_PREFIX + "/login")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(user)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(String.format("{\"message\":\"usernameOrEmail cannot be empty \",\"errorCode\":\"%s\"}", BAD_REQUEST.getReasonPhrase())));
    }

    @Test
    @Parameters({"Corinne Conway, Mckay, corinnemckay@zidox.com, Hopkins"})
    public void should_register_user_and_return_success_message(String name, String username, String email, String password) throws Exception {
        SignUpRequest user = new SignUpRequest(name, username, email, password);

        Mockito.doReturn(Optional.of(new Role())).when(roleRepository).findByName(Mockito.any(RoleName.class));
        Mockito.doReturn(new User(name, username, email, password)).when(userRepository).save(Mockito.any(User.class));

        restMvc.perform(post(RESOURCE_PREFIX + "/register")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(user)))
                .andExpect(status().isOk())
                .andExpect(content().string(String.format("{\"success\":true,\"message\":\"User %s registered successfully!\"}", username)));
    }

    @Test
    @Parameters({"Corinne Conway, Mckay, corinnemckay@zidox.com, Hopkins"})
    public void should_500_when_default_role_is_not_saved(String name, String username, String email, String password) throws Exception {
        SignUpRequest user = new SignUpRequest(name, username, email, password);

        Mockito.doReturn(Optional.empty()).when(roleRepository).findByName(Mockito.any(RoleName.class));

        restMvc.perform(post(RESOURCE_PREFIX + "/register")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(user)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(String.format("{\"message\":\"User Role not set.\",\"errorCode\":\"%s\"}", INTERNAL_SERVER_ERROR.getReasonPhrase())));
    }

    @Test
    public void should_400_when_name_or_username_or_email_or_password_not_valid() throws Exception {
        SignUpRequest user = new SignUpRequest(null, "azerty", "corinnemckay@zidox.com", "azerty");

        restMvc.perform(post(RESOURCE_PREFIX + "/register")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(user)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(String.format("{\"message\":\"Name cannot be empty \",\"errorCode\":\"%s\"}", BAD_REQUEST.getReasonPhrase())));
    }

    @Test
    @Parameters({"Corinne Conway, Mckay, corinnemckay@zidox.com, Hopkins"})
    public void should_400_when_username_exists_and_return_error_message(String name, String username, String email, String password) throws Exception {
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
    public void should_400_when_email_exists_and_return_error_message(String name, String username, String email, String password) throws Exception {
        SignUpRequest user = new SignUpRequest(name, username, email, password);
        Mockito.doReturn(true).when(userRepository).existsByEmail(email);
        restMvc.perform(post(RESOURCE_PREFIX + "/register")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(user)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("{\"success\":false,\"message\":\"Email Address already in use!\"}"));
    }

}
