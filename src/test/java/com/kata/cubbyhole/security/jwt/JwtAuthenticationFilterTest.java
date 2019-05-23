package com.kata.cubbyhole.security.jwt;

import com.kata.cubbyhole.runner.SpringJUnitParams;
import com.kata.cubbyhole.security.CustomUserDetailsService;
import com.kata.cubbyhole.security.mapper.UserPrincipal;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import static com.kata.cubbyhole.config.Constants.AUTHORIZATION_HEADER;
import static com.kata.cubbyhole.config.Constants.AUTHORIZATION_PREFIX;
import static com.kata.cubbyhole.model.enumeration.RoleName.ROLE_USER;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnitParams.class)
@SpringBootTest
public class JwtAuthenticationFilterTest {

    @Autowired
    @Spy
    private JwtTokenProvider jwtTokenProvider;

    @Spy
    private CustomUserDetailsService customUserDetailsService;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private PodamFactory podamFactory;

    @Before
    public void setup() {
        podamFactory = new PodamFactoryImpl();
        MockitoAnnotations.initMocks(this);
    }


    @Test
    @Parameters({"/api/test"})
    public void should_dofilter_with_valid_token_and_return_authentication(String url) throws Exception {
        Collection<? extends GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(ROLE_USER.name()));
        UserPrincipal userPrincipal = podamFactory.manufacturePojo(UserPrincipal.class);
        userPrincipal.setAuthorities(authorities);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userPrincipal,
                null,
                authorities);
        String token = jwtTokenProvider.generateToken(authentication);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(AUTHORIZATION_HEADER, AUTHORIZATION_PREFIX + token);
        request.setRequestURI(url);

        Mockito.doReturn(userPrincipal).when(customUserDetailsService).loadUserById(userPrincipal.getId());

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo(userPrincipal.getUsername());
        assertThat(SecurityContextHolder.getContext().getAuthentication().getCredentials().toString()).isEqualTo(token);
    }

    @Test
    @Parameters({"/api/test"})
    public void should_dofilter_with_invalid_token_and_return_null_authentication(String url) throws ServletException, IOException {
        String token = "wrong_jwt";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(AUTHORIZATION_HEADER, AUTHORIZATION_PREFIX + token);
        request.setRequestURI(url);

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

    }

    @Test
    @Parameters({"/api/test"})
    public void should_dofilter_with_missing_authorization_and_return_null_authentication(String url) throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(url);

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @Parameters({"/api/test"})
    public void should_dofilter_with_missing_token_and_return_null_authentication(String url) throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(AUTHORIZATION_HEADER, AUTHORIZATION_PREFIX);
        request.setRequestURI(url);

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

}
