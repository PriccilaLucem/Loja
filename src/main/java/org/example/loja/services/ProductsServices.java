package org.example.loja.services;

import org.example.loja.dto.ProductDTO;
import org.example.loja.entities.ImageEntity;
import org.example.loja.entities.ProductEntity;
import org.example.loja.repository.CategoryRepository;
import org.example.loja.repository.ImageRepository;
import org.example.loja.repository.ProductsRepository;
import org.example.loja.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductsServices {
    @Autowired
    private ProductsRepository productsRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ImageRepository imageRepository;

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
        product.setQuantity(newQuantity);
        productsRepository.save(product);

        return product.getQuantity();
    }
    public List<ProductEntity> getProductsByCategories(String category){
        return productsRepository.findAllByCategories_Name(category);
    }
    public ProductEntity createProduct(ProductDTO productDTO){
        ProductEntity product = new ProductEntity();
        product.setName(productDTO.getName());
        product.setPrice(productDTO.getPrice());
        product.setQuantity(productDTO.getQuantity());
        product.setBrand(productDTO.getBrand());
        Set<ImageEntity> imageEntities = productDTO.getImages().stream()
                .map(url -> imageRepository.findByUrl(url)
                        .orElseGet(() -> {
                            ImageEntity newImage = new ImageEntity();
                            newImage.setUrl(url);
                            return imageRepository.save(newImage);
                        })
                ).collect(Collectors.toSet());
            product.setStore(storeRepository.findById(productDTO.getStoreId()).orElseThrow(() -> new IllegalArgumentException("Invalid Store")));
        product.setCategories(new HashSet<>(categoryRepository.findAllById(productDTO.getCategories())));

        validateProduct(product);
        return productsRepository.save(product);
    }

    public boolean updateProduct(ProductEntity product){
        validateProduct(product);
        productsRepository.save(product);
        return true;
    }
    public void validateProduct(ProductEntity product) throws IllegalArgumentException{
        if(product == null){
            throw new IllegalArgumentException("Invalid Product object");
        }
        if(product.getName().isBlank() || product.getPrice() < 0){
            throw new IllegalArgumentException("Invalid Product Price");
        }
        if(product.getQuantity() < 0){
            throw new IllegalArgumentException("Invalid Product Quantity");
        }
        if(product.getCategories().isEmpty()){
            throw new IllegalArgumentException("Invalid Product Categories");
        }
        if(product.getStore().getId() == null){
            throw new IllegalArgumentException("Invalid Product Store");
        }
        for (ImageEntity img : product.getImage()) {
            if (img.getUrl() == null || !img.getUrl().startsWith("data:image/")) {
                throw new IllegalArgumentException("Invalid Product Image format");
            }
        }
    }

}
