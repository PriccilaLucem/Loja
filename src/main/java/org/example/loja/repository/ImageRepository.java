package org.example.loja.repository;

import org.example.loja.entities.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageRepository  extends JpaRepository<ImageEntity, Long> {
    Optional<ImageEntity> findByUrl(String url);
}
