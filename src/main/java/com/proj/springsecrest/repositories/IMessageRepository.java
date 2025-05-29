package com.proj.springsecrest.repositories;

import com.proj.springsecrest.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IMessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findByEmployeeCode(UUID employeeCode);
}