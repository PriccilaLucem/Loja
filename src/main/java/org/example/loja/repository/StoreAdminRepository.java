package org.example.loja.repository;

import org.example.loja.entities.StoreAdminEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface StoreAdminRepository extends JpaRepository<StoreAdminEntity, UUID> {

    StoreAdminEntity findByEmail(String email);

    @Modifying
    @Transactional
    @Query("UPDATE StoreAdminEntity s SET s.status = :status WHERE s.id = :id")
    int updateStatus(@Param("id") UUID id, @Param("status") boolean status);
}