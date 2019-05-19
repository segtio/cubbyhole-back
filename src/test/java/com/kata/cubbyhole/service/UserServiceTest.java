package com.kata.cubbyhole.service;

import com.kata.cubbyhole.runner.SpringJUnitParams;
import com.kata.cubbyhole.security.jwt.JwtTokenProvider;
import com.kata.cubbyhole.security.mapper.UserPrincipal;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private PodamFactory podamFactory;

    @Before
    public void setup() {
        podamFactory = new PodamFactoryImpl();
    }


    @Test
    @Parameters({"azerty", "azerty"})
    public void should_authenticate_user_from_username_or_email_and_password(String usernameOrEmail, String password) {
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
}
