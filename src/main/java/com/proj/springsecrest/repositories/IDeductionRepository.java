package com.proj.springsecrest.repositories;

import com.proj.springsecrest.enums.ERole;
import com.proj.springsecrest.models.Deduction;
import com.proj.springsecrest.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IDeductionRepository extends JpaRepository<Deduction, UUID> {
    Optional<Deduction> findByCode(UUID name);
    Optional<Deduction> findByDeductionName(String name);

}