package com.kata.cubbyhole.repository;

import com.kata.cubbyhole.model.Role;
import com.kata.cubbyhole.model.enumeration.RoleName;
import com.kata.cubbyhole.runner.SpringJUnitParams;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringJUnitParams.class)
@SpringBootTest
@Transactional
public class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    @Parameters({"ROLE_ADMIN, true", "azerty, false"})
    public void should_find_by_name(String roleName, boolean isPresent) {
        Optional<Role> roleOptional = roleRepository.findByName(RoleName.getRoleName(roleName));

        assertThat(roleOptional.isPresent()).isEqualTo(isPresent);
    }
}
