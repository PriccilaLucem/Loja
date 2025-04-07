package org.example.loja.repository;

import org.example.loja.entities.AdminMasterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AdminMasterRepository extends JpaRepository<AdminMasterEntity, UUID> {
    Optional<AdminMasterEntity> findByEmail(String email);
}
