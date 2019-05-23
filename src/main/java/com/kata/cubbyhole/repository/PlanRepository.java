package com.kata.cubbyhole.repository;

import com.kata.cubbyhole.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "plan", path = "plan")
public interface PlanRepository extends JpaRepository<Plan, Long> {
}
