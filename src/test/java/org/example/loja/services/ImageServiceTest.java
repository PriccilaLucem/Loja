package org.example.loja.services;

import org.example.loja.entities.ImageEntity;
import org.example.loja.entities.ProductEntity;
import org.example.loja.entities.StoreEntity;
import org.example.loja.repository.ImageRepository;
import org.example.loja.repository.ProductsRepository;
import org.example.loja.repository.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ImageServiceTest {

    @Mock
    private ImageRepository imageRepository;
    @Mock
    private StoreRepository storeRepository;
    @Mock
    private ProductsRepository productsRepository;
    @InjectMocks
    private ImageService imageService;

    @BeforeEach
    void setUp() {
        imageRepository = mock(ImageRepository.class);
        storeRepository = mock(StoreRepository.class);
        productsRepository = mock(ProductsRepository.class);
        imageService = new ImageService();

    }

    @Test
    void testSaveStoreImage_Success() {
        String storeName = "TestStore";
        StoreEntity store = new StoreEntity();
        store.setName(storeName);
        ImageEntity image = new ImageEntity();
        image.setFileName("test.png");

        when(storeRepository.findOneByName(storeName)).thenReturn(Optional.of(store));
        when(imageRepository.save(image)).thenReturn(image);

        String result = imageService.saveStoreImage(image, storeName);

        assertEquals(image.getUrl(), result);
        assertEquals(store, image.getStore());
        verify(imageRepository, times(1)).save(image);
    }

    @Test
    void testSaveProductImage_Success() {
        String storeName = "TestStore";
        Long productId = 1L;
        StoreEntity store = new StoreEntity();
        store.setId(10L);
        store.setName(storeName);

        ProductEntity product = new ProductEntity();
        product.setId(productId);
        product.setStore(store);

        ImageEntity image = new ImageEntity();
        image.setFileName("product.png");

        when(storeRepository.findOneByName(storeName)).thenReturn(Optional.of(store));
        when(productsRepository.findById(productId)).thenReturn(Optional.of(product));
        when(imageRepository.save(image)).thenReturn(image);

        String result = imageService.saveProductImage(image, productId, storeName);

        assertEquals(image.getUrl(), result);
        assertEquals(product, image.getProduct());
        assertEquals(store, image.getStore());
        verify(imageRepository).save(image);
    }

    @Test
    void testSaveProductImage_ThrowsIfStoreMismatch() {
        String storeName = "TestStore";
        Long productId = 1L;

        StoreEntity store = new StoreEntity();
        store.setId(1L);

        StoreEntity otherStore = new StoreEntity();
        otherStore.setId(2L);

        ProductEntity product = new ProductEntity();
        product.setId(productId);
        product.setStore(otherStore);

        ImageEntity image = new ImageEntity();

        when(storeRepository.findOneByName(storeName)).thenReturn(Optional.of(store));
        when(productsRepository.findById(productId)).thenReturn(Optional.of(product));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> imageService.saveProductImage(image, productId, storeName));

        assertEquals("Product does not belong to the specified store", exception.getMessage());
        verify(imageRepository, never()).save(any());
    }
}
