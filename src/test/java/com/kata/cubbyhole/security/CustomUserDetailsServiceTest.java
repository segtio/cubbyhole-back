package com.kata.cubbyhole.security;

import com.kata.cubbyhole.model.User;
import com.kata.cubbyhole.repository.UserRepository;
import com.kata.cubbyhole.runner.SpringJUnitParams;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringJUnitParams.class)
@SpringBootTest
public class CustomUserDetailsServiceTest {

    @Spy
    private UserRepository userRepository;

    private PodamFactory podamFactory;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Before
    public void setup() {
        podamFactory = new PodamFactoryImpl();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @Parameters({"azerty"})
    public void should_load_user_by_username_and_return_user_details(String username) {
        User user = podamFactory.manufacturePojo(User.class);
        Mockito.doReturn(Optional.of(user)).when(userRepository).findByUsernameOrEmail(username, username);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        assertThat(userDetails.getUsername()).isEqualTo(user.getUsername());
    }

    @Test(expected = UsernameNotFoundException.class)
    @Parameters({"azerty"})
    public void should_load_user_by_username_and_throws_UsernameNotFoundException(String username) {
        Mockito.doReturn(Optional.empty()).when(userRepository).findByUsernameOrEmail(username, username);
        customUserDetailsService.loadUserByUsername(username);
    }

    @Test
    @Parameters({"568"})
    public void should_load_user_by_id_and_return_user_details(Long id) {
        User user = podamFactory.manufacturePojo(User.class);
        Mockito.doReturn(Optional.of(user)).when(userRepository).findById(id);

        UserDetails userDetails = customUserDetailsService.loadUserById(id);

        assertThat(userDetails.getUsername()).isEqualTo(user.getUsername());
    }

    @Test(expected = UsernameNotFoundException.class)
    @Parameters({"568"})
    public void should_load_user_by_id_and_throws_UsernameNotFoundException(Long id) {
        Mockito.doReturn(Optional.empty()).when(userRepository).findById(id);
        customUserDetailsService.loadUserById(id);
    }
}
