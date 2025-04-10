package org.example.loja.services;

import org.example.loja.dto.ProductDTO;
import org.example.loja.entities.CategoryEntity;
import org.example.loja.entities.ProductEntity;
import org.example.loja.repository.CategoryInterface;
import org.example.loja.repository.ProductsRepository;
import org.example.loja.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
public class ProductsServices {
    @Autowired
    private ProductsRepository productsRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private CategoryInterface categoryInterface;
    public List<ProductEntity> getAllProducts(){
        return productsRepository.findAll();
    }
    public boolean deleteProduct(Long id){
        int affectedRows = productsRepository.deleteProductEntitiesById(id);
        return affectedRows > 0;
    }

    public ProductEntity getProductById(Long id){
        return productsRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid Product"));
    }

    public int updateProductQuantity(Long productId, int quantityChange) {
        ProductEntity product = productsRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("Invalid Product"));
        int newQuantity = product.getQuantity() + quantityChange;

        if (newQuantity < 0) {
            throw new IllegalArgumentException("Resulting product quantity cannot be less than zero");
        }

        productsRepository.save(product);

        return product.getQuantity();
    }
    public List<ProductEntity> getProductsByCategories(String category){
        return productsRepository.findAllByCategories_Name(category);
    }
    public long createProduct(ProductDTO productDTO){
        ProductEntity product = new ProductEntity();
        product.setName(productDTO.getName());
        product.setPrice(productDTO.getPrice());
        product.setQuantity(productDTO.getQuantity());
        product.setBrand(productDTO.getBrand());
        product.setImage(productDTO.getImage());
        product.setStore(storeRepository.findById(productDTO.getStoreId()).orElseThrow(() -> new IllegalArgumentException("Invalid Store")));
        product.setCategories(new HashSet<>(categoryInterface.findAllById(productDTO.getCategories())));

        validateProduct(product);
        productsRepository.save(product);
        return product.getId();
    }

    public boolean updateProduct(ProductEntity product){
        try {
            productsRepository.save(product);
            return true;
        }catch (Exception e){
            return false;
        }
    }
    public void validateProduct(ProductEntity product) throws IllegalArgumentException{
        if(product == null){
            throw new IllegalArgumentException("Invalid Product");
        }
        if(product.getName().isBlank() || product.getPrice() < 0){
            throw new IllegalArgumentException("Invalid Product");
        }
        if(product.getQuantity() < 0){
            throw new IllegalArgumentException("Invalid Product");
        }
        if(product.getCategories().isEmpty()){
            throw new IllegalArgumentException("Invalid Product");
        }
        if(product.getStore().getId() == null){
            throw new IllegalArgumentException("Invalid Product");
        }
        if(product.getImage().isBlank() || !product.getImage().startsWith("data:image/")){
            throw new IllegalArgumentException("Invalid Product");
        }
    }
}
