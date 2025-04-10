package org.example.loja.repository;

import org.example.loja.entities.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductsRepository extends JpaRepository<ProductEntity, Long> {

    int deleteProductEntitiesById(Long id);

}
