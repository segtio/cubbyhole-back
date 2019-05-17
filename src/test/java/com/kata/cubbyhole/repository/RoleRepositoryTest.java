package com.kata.cubbyhole.repository;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kata.cubbyhole.model.Role;
import com.kata.cubbyhole.model.enumeration.RoleName;
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
public class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Value(value = "classpath:repository/roles.json")
    private Resource rolesJson;

    @Before
    public void setup() throws IOException {
        roleRepository.deleteAll();
        Type listType = new TypeToken<ArrayList<Role>>() {
        }.getType();
        List<Role> roles = new Gson().fromJson(StreamUtils.copyToString(rolesJson.getInputStream(), Charset.defaultCharset()), listType);
        roleRepository.saveAll(roles);
    }

    @Test
    @Parameters({"ROLE_ADMIN, true", "azerty, false"})
    public void should_find_by_name(String roleName, boolean isPresent) {
        Optional<Role> roleOptional = roleRepository.findByName(RoleName.getRoleName(roleName));

        assertThat(roleOptional.isPresent()).isEqualTo(isPresent);
    }
}
