package com.kata.cubbyhole.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Before
    public void setup() {
        userRepository.deleteAll();
    }

    @Test
    public void should_find_by_email() {

    }

    @Test
    public void should_find_by_email_or_username() {

    }

    @Test
    public void should_find_by_username() {

    }

    @Test
    public void should_check_if_exist_by_username() {

    }

    @Test
    public void should_check_if_exist_by_email() {

    }
}
