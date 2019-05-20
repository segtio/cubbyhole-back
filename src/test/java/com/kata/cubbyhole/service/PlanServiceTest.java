package com.kata.cubbyhole.service;

import com.kata.cubbyhole.model.Plan;
import com.kata.cubbyhole.repository.PlanRepository;
import com.kata.cubbyhole.runner.SpringJUnitParams;
import com.kata.cubbyhole.service.impl.PlanServiceImpl;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringJUnitParams.class)
@SpringBootTest
@Transactional
public class PlanServiceTest {

    private PlanService planService;

    @Autowired
    private PlanRepository planRepository;


    @Before
    public void setup() {
        this.planService = new PlanServiceImpl(planRepository);
    }

    @Test
    @Parameters({"Gold, 100, 60, 100"})
    public void should_save_plan(String name, Double price, Long duration, Long storagespace) {
        Plan plan = new Plan(name, price, duration, storagespace);
        Plan CreatedPlan = planService.save(plan);

        assertThat(CreatedPlan).isNotNull();
        assertThat(CreatedPlan.getId()).isNotNull();
    }

    @Test
    public void should_find_all_plans() {
        List<Plan> plans = planService.findAll();

        assertThat(plans).isNotEmpty();
    }

    @Test
    public void should_find_plan_by_id() {
        Optional<Plan> plan = planService.findById(1L);

        assertThat(plan.isPresent()).isTrue();
        assertThat(plan.get().getId()).isEqualTo(1L);
    }

    @Test
    public void should_delete_plan() {
        List<Plan> plans = planService.findAll();
        planService.deleteById(plans.get(0).getId());

        Optional<Plan> plan = planService.findById(plans.get(0).getId());

        assertThat(plan.isPresent()).isFalse();
    }
}
