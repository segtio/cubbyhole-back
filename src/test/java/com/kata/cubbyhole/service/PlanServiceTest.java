package com.kata.cubbyhole.service;

import com.kata.cubbyhole.model.Plan;
import com.kata.cubbyhole.runner.SpringJUnitParams;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringJUnitParams.class)
@SpringBootTest
@Transactional
public class PlanServiceTest {

    private PlanService planService;

    private PodamFactory podamFactory;


    @Before
    public void setup() {
        podamFactory = new PodamFactoryImpl();
    }

    @Test
    public void should_save_plan() {
        Plan plan = podamFactory.manufacturePojo(Plan.class);
        Plan CreatedPlan = planService.save(plan);

        assertThat(CreatedPlan).isNotNull();
        assertThat(CreatedPlan.getId()).isNotNull();
    }

    @Test
    public void should_find_all_plans() {
        List<Plan> plans = planService.findAll();

        assertThat(plans).isNotEmpty();
        assertThat(plans.size()).isEqualTo(2);
    }

    @Test
    public void should_find_plan_by_id() {
        Plan plan = planService.findOne(1L);

        assertThat(plan).isNotNull();
        assertThat(plan.getId()).isEqualTo(1L);
    }

    @Test
    public void should_delete_plan() {
        List<Plan> plans = planService.findAll();
        planService.delete(plans.get(0).getId());

        Plan plan = planService.findOne(plans.get(0).getId());

        assertThat(plan).isNull();
    }
}
