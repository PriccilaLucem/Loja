package org.example.loja.services;

import org.example.loja.entities.ImageEntity;
import org.example.loja.entities.ProductEntity;
import org.example.loja.entities.StoreEntity;
import org.example.loja.repository.ImageRepository;
import org.example.loja.repository.ProductsRepository;
import org.example.loja.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ProductsRepository productsRepository;

    public String saveStoreImage(ImageEntity image, String storeName) {
        StoreEntity store = (StoreEntity) storeRepository.findOneByName(storeName)
                .orElseThrow(() -> new RuntimeException("No store found"));
        image.setStore(store);
        return imageRepository.save(image).getUrl();
    }

    public String saveProductImage(ImageEntity image, Long productId, String storeName) {
        StoreEntity store = (StoreEntity) storeRepository.findOneByName(storeName)
                .orElseThrow(() -> new RuntimeException("No store found"));

        ProductEntity product = productsRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("No product found"));

        if (!product.getStore().getId().equals(store.getId())) {
            throw new RuntimeException("Product does not belong to the specified store");
        }

        image.setProduct(product);
        image.setStore(store); // Optional: if you want to link both

        return imageRepository.save(image).getUrl();
    }
}
