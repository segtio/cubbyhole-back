package com.kata.cubbyhole.repository;

import com.kata.cubbyhole.model.User;
import com.kata.cubbyhole.runner.SpringJUnitParams;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;


@RunWith(SpringJUnitParams.class)
@SpringBootTest
@Transactional
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    @SqlGroup({
            @Sql(executionPhase = BEFORE_TEST_METHOD, scripts = "/data/users.sql"),
            @Sql(executionPhase = AFTER_TEST_METHOD, scripts = "/data/cleanup_users.sql")
    })
    @Parameters({"corinnemckay@zidox.com, Corinne Conway, true"})
    public void should_find_by_email(String email, String name, boolean isPresent) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        assertThat(userOptional.isPresent()).isEqualTo(isPresent);
        assertThat(userOptional.get().getName()).isEqualTo(name);
    }

    @Test
    @SqlGroup({
            @Sql(executionPhase = BEFORE_TEST_METHOD, scripts = "/data/users.sql"),
            @Sql(executionPhase = AFTER_TEST_METHOD, scripts = "/data/cleanup_users.sql")
    })
    @Parameters({"Mendoza, Mendoza@zidox.com, Talley Heath, true", "hamptonwebb, hamptonwebb@zidox.com, Hampton Mcintyre, true"})
    public void should_find_by_username_or_email(String username, String email, String name, boolean isPresent) {
        Optional<User> userOptional = userRepository.findByUsernameOrEmail(username, email);

        assertThat(userOptional.isPresent()).isEqualTo(isPresent);
        assertThat(userOptional.get().getName()).isEqualTo(name);
    }

    @Test
    @SqlGroup({
            @Sql(executionPhase = BEFORE_TEST_METHOD, scripts = "/data/users.sql"),
            @Sql(executionPhase = AFTER_TEST_METHOD, scripts = "/data/cleanup_users.sql")
    })
    @Parameters({"Mendoza, Talley Heath, true"})
    public void should_find_by_username(String username, String name, boolean isPresent) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        assertThat(userOptional.isPresent()).isEqualTo(isPresent);
        assertThat(userOptional.get().getName()).isEqualTo(name);
    }

    @Test
    @SqlGroup({
            @Sql(executionPhase = BEFORE_TEST_METHOD, scripts = "/data/users.sql"),
            @Sql(executionPhase = AFTER_TEST_METHOD, scripts = "/data/cleanup_users.sql")
    })
    @Parameters({"Mendoza, true", "azerty, false"})
    public void should_check_if_exist_by_username(String username, boolean exists) {
        boolean isUsername = userRepository.existsByUsername(username);

        assertThat(isUsername).isEqualTo(exists);
    }

    @Test
    @SqlGroup({
            @Sql(executionPhase = BEFORE_TEST_METHOD, scripts = "/data/users.sql"),
            @Sql(executionPhase = AFTER_TEST_METHOD, scripts = "/data/cleanup_users.sql")
    })
    @Parameters({"hamptonwebb@zidox.com, true"})
    public void should_check_if_exist_by_email(String email, boolean exists) {
        boolean isUsername = userRepository.existsByEmail(email);

        assertThat(isUsername).isEqualTo(exists);
    }
}
