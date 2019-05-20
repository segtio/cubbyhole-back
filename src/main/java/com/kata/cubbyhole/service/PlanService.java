package com.kata.cubbyhole.service;

import com.kata.cubbyhole.model.Plan;

import java.util.List;
import java.util.Optional;

public interface PlanService {

    Plan save(Plan plan);

    List<Plan> findAll();

    Optional<Plan> findById(Long id);

    void deleteById(Long id);
}
