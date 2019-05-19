package com.kata.cubbyhole.service;

import com.kata.cubbyhole.model.User;
import com.kata.cubbyhole.repository.UserRepository;
import com.kata.cubbyhole.runner.SpringJUnitParams;
import com.kata.cubbyhole.security.jwt.JwtTokenProvider;
import com.kata.cubbyhole.security.mapper.UserPrincipal;
import com.kata.cubbyhole.service.impl.UserServiceImpl;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.transaction.annotation.Transactional;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.util.Collections;

import static com.kata.cubbyhole.model.enumeration.RoleName.ROLE_USER;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnitParams.class)
@SpringBootTest
@Transactional
public class UserServiceTest {


    @Autowired
    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    @Mock
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    private UserService userService;

    private PodamFactory podamFactory;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        userService = new UserServiceImpl(authenticationManager, jwtTokenProvider);
        podamFactory = new PodamFactoryImpl();
    }


    @Test
    @Parameters({"azerty, azerty"})
    public void should_authenticate_user_from_username_or_email_and_password(String usernameOrEmail, String password) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                usernameOrEmail,
                password,
                Collections.singletonList(new SimpleGrantedAuthority(ROLE_USER.name()))
        );
        Mockito.doReturn(usernamePasswordAuthenticationToken)
                .when(authenticationManager)
                .authenticate(Mockito.any(Authentication.class));

        Authentication authentication = userService.authenticate(usernameOrEmail, password);

        assertThat(authentication.isAuthenticated()).isTrue();
    }

    @Test
    public void should_generate_token_from_authenticated() {
        UserPrincipal userPrincipal = podamFactory.manufacturePojo(UserPrincipal.class);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userPrincipal,
                null,
                Collections.singletonList(new SimpleGrantedAuthority(ROLE_USER.name())));

        String token = userService.generateToken(authentication);

        assertThat(token).isNotEmpty();
        assertThat(jwtTokenProvider.validateToken(token)).isTrue();
    }

    @Test
    @Parameters({"Corinne Conway, Mckay, corinnemckay@zidox.com, Hopkins"})
    public void should_register_user(String name, String username, String email, String password) {
        User user = userService.register(name, username, email, password);

        boolean registered = userRepository.existsByUsername(user.getUsername());

        assertThat(registered).isTrue();
    }
}
