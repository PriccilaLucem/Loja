package org.example.loja.repository;

import lombok.NonNull;
import org.example.loja.entities.StoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface StoreRepository extends JpaRepository<StoreEntity, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE StoreEntity s SET s.active = false WHERE s.id =:id")
    int deactivateStoreById(@Param("id") long id);

    @Modifying
    @Query("UPDATE StoreEntity s SET s.name = :#{#storeEntity.name}, " +
            "s.description = :#{#storeEntity.description}, " +
            "s.phone = :#{#storeEntity.phone}, " +
            "s.email = :#{#storeEntity.email} " +
            "WHERE s.id = :#{#storeEntity.id}")
    @Transactional
    int saveStoreAndReturnAffectedRows(@NonNull @Param("storeEntity") StoreEntity storeEntity);

}
