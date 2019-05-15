package com.kata.cubbyhole.repository;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kata.cubbyhole.model.User;
import com.kata.cubbyhole.runner.SpringJUnitParams;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringJUnitParams.class)
@SpringBootTest
@Transactional
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Value(value = "classpath:repository/users.json")
    private Resource usersJson;

    @Before
    public void setup() throws IOException {
        userRepository.deleteAll();
        Type listType = new TypeToken<ArrayList<User>>() {
        }.getType();
        List<User> users = new Gson().fromJson(StreamUtils.copyToString(usersJson.getInputStream(), Charset.defaultCharset()), listType);
       userRepository.saveAll(users);
    }

    @Test
    @Parameters({"corinnemckay@zidox.com, Corinne Conway, true"})
    public void should_find_by_email(String email, String name, boolean isPresent) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        assertThat(userOptional.isPresent()).isEqualTo(isPresent);
        assertThat(userOptional.get().getName()).isEqualTo(name);
    }

    @Test
    @Parameters({"Mendoza, Mendoza@zidox.com, Talley Heath, true", "hamptonwebb, hamptonwebb@zidox.com, Hampton Mcintyre, true"})
    public void should_find_by_username_or_email(String username, String email, String name, boolean isPresent) {
        Optional<User> userOptional = userRepository.findByUsernameOrEmail(username, email);

        assertThat(userOptional.isPresent()).isEqualTo(isPresent);
        assertThat(userOptional.get().getName()).isEqualTo(name);
    }

    @Test
    @Parameters({"Mendoza, Talley Heath, true"})
    public void should_find_by_username(String username, String name, boolean isPresent) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        assertThat(userOptional.isPresent()).isEqualTo(isPresent);
        assertThat(userOptional.get().getName()).isEqualTo(name);
    }

    @Test
    @Parameters({"Mendoza, true", "azerty, false"})
    public void should_check_if_exist_by_username(String username, boolean exists) {
        boolean isUsername = userRepository.existsByUsername(username);

        assertThat(isUsername).isEqualTo(exists);
    }

    @Test
    @Parameters({"hamptonwebb@zidox.com, true"})
    public void should_check_if_exist_by_email(String email, boolean exists) {
        boolean isUsername = userRepository.existsByEmail(email);

        assertThat(isUsername).isEqualTo(exists);
    }
}
