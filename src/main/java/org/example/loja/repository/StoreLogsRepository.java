package org.example.loja.repository;

import org.example.loja.entities.AdminLogsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreLogsRepository extends JpaRepository<AdminLogsEntity, Long> {
}
