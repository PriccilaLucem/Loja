package org.example.loja.repository;

import org.example.loja.entities.StoreManagerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StoreManagerRepository extends JpaRepository<StoreManagerEntity, UUID> {
    Optional<StoreManagerEntity> findByEmail(String email);

    @Modifying
    @Transactional
    @Query("UPDATE StoreAdminEntity s SET s.status = true, s.managedStore = :storeId  WHERE s.id = :id")
    int updateStatusToTrue(UUID id, Long storeId);

    @Modifying
    @Transactional
    @Query
    int deleteStoreManagerById(UUID id);
}
