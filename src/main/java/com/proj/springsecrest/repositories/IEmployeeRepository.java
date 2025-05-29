package com.proj.springsecrest.repositories;

import com.proj.springsecrest.enums.ERole;
import com.proj.springsecrest.models.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IEmployeeRepository extends JpaRepository<Employee, UUID> {

    Optional<Employee> findByCode(UUID userID);

    Optional<Employee> findByTelephone(String telephone);
    Optional<Employee> findByEmail(String email);

    Page<Employee> findByRoles(Pageable pageable, ERole role);

    Optional<Employee> findByActivationCode(String activationCode);

    @Query("SELECT u FROM Employee u" +
            " WHERE (lower(u.firstName)  LIKE ('%' || lower(:searchKey) || '%')) " +
            " OR (lower(u.lastName) LIKE ('%' || lower(:searchKey) || '%')) " +
            " OR (lower(u.email) LIKE ('%' || lower(:searchKey) || '%'))")
    Page<Employee> searchEmployee(Pageable pageable, String searchKey);
}
