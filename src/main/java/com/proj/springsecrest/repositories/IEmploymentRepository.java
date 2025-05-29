package com.proj.springsecrest.repositories;

import com.proj.springsecrest.enums.EEmployeementStatus;
import com.proj.springsecrest.enums.ERole;
import com.proj.springsecrest.models.Employee;
import com.proj.springsecrest.models.Employment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IEmploymentRepository extends JpaRepository<Employment, UUID> {

    Optional<Employment> findByCode(UUID userID);

    List<Employment> findByStatus(EEmployeementStatus status);
//    Employment create(Employment employment);

//    Employment save(Employment employment);
    @Query("SELECT e FROM Employment e WHERE e.employee.code = :searchKey")
    Optional<Employment> findByEmployeeCode(@Param("searchKey") UUID searchKey);
}
