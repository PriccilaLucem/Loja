package org.example.loja.repository;

import org.example.loja.entities.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, UUID> {


    int deleteEmployeeEntitiesBy(UUID id);
}
