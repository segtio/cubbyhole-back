package com.kata.cubbyhole.security.jwt;


import com.kata.cubbyhole.runner.SpringJUnitParams;
import com.kata.cubbyhole.security.mapper.UserPrincipal;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.StreamUtils;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;

import static com.kata.cubbyhole.model.enumeration.RoleName.ROLE_USER;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnitParams.class)
@SpringBootTest
public class JwtTokenProviderTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value(value = "classpath:repository/jwt.txt")
    private Resource tokenResource;

    private PodamFactory podamFactory;


    @Before
    public void setup() {
        podamFactory = new PodamFactoryImpl();
    }

    @Test
    public void should_generate_token() {
        UserPrincipal userPrincipal = podamFactory.manufacturePojo(UserPrincipal.class);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userPrincipal,
                null,
                Collections.singletonList(new SimpleGrantedAuthority(ROLE_USER.name())));

        String token = jwtTokenProvider.generateToken(authentication);

        assertThat(token).isNotEmpty();
        assertThat(jwtTokenProvider.validateToken(token)).isTrue();
    }

    @Test
    public void should_get_userId_from_JWT() throws IOException {
        String[] tokenResourceTab = StreamUtils.copyToString(tokenResource.getInputStream(), Charset.defaultCharset()).split("\\|\\|");
        Long expected = Long.parseLong(tokenResourceTab[0]);
        String token = tokenResourceTab[1];
        Long id = jwtTokenProvider.getUserIdFromJWT(token);

        assertThat(id).isEqualTo(expected);
    }
}
