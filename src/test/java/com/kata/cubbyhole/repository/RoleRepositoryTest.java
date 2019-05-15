package com.kata.cubbyhole.repository;

import com.kata.cubbyhole.runner.SpringJUnitParams;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnitParams.class)
@SpringBootTest
@Transactional
public class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    public void should_find_by_name() {

    }
}
