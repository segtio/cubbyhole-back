package com.kata.cubbyhole.service;

import com.kata.cubbyhole.model.Plan;

import java.util.List;

public interface PlanService {

    Plan save(Plan plan);

    List<Plan> findAll();

    Plan findOne(Long id);

    void delete(Long id);
}
