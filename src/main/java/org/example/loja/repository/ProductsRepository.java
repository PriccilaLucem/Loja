package org.example.loja.repository;

import org.example.loja.entities.CategoryEntity;
import org.example.loja.entities.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductsRepository extends JpaRepository<ProductEntity, Long> {

    int deleteProductEntitiesById(Long id);

    List<ProductEntity> findAllByCategories_Name(String categoryName);
}