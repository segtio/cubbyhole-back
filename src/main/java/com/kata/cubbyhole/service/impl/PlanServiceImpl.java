package com.kata.cubbyhole.service.impl;

import com.kata.cubbyhole.model.Plan;
import com.kata.cubbyhole.repository.PlanRepository;
import com.kata.cubbyhole.service.PlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PlanServiceImpl implements PlanService {

    private final PlanRepository planRepository;

    @Autowired
    public PlanServiceImpl(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    @Override
    public Plan save(Plan plan) {
        return planRepository.save(plan);
    }

    @Override
    public List<Plan> findAll() {
        return planRepository.findAll();
    }

    @Override
    public Optional<Plan> findById(Long id) {
        return planRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        planRepository.deleteById(id);
    }
}
