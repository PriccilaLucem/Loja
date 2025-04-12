package org.example.loja.services;

import org.example.loja.dto.ProductDTO;
import org.example.loja.entities.CategoryEntity;
import org.example.loja.entities.ProductEntity;
import org.example.loja.entities.StoreEntity;
import org.example.loja.repository.CategoryRepository;
import org.example.loja.repository.ProductsRepository;
import org.example.loja.repository.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductsServicesTest {

    @InjectMocks
    private ProductsServices productsServices;

    @Mock
    private ProductsRepository productsRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private ProductDTO getValidProductDTO() {
        ProductDTO dto = new ProductDTO();
        dto.setName("Produto Teste");
        dto.setPrice(100.0);
        dto.setQuantity(10);
        dto.setBrand("Marca");
        dto.setImage("data:image/aaaa");
        dto.setStoreId(1L);
        dto.setCategories(Set.of(1L));
        return dto;
    }

    @Test
    void testCreateProduct_Success() {
        ProductDTO dto = getValidProductDTO();
        StoreEntity store = new StoreEntity();
        store.setId(1L);

        CategoryEntity category = new CategoryEntity();
        category.setId(1L);
        category.setName("Tv");
        category.setDescription("TV");

        when(storeRepository.findById(1L)).thenReturn(Optional.of(store));
        when(categoryRepository.findAllById(any())).thenReturn(List.of(category));

        ProductEntity savedProduct = new ProductEntity();
        savedProduct.setName(dto.getName());

        when(productsRepository.save(any())).thenReturn(savedProduct);

        ProductEntity result = productsServices.createProduct(dto);
        assertEquals(dto.getName(), result.getName());
    }

    @Test
    void testCreateProduct_InvalidImage() {
        ProductDTO dto = getValidProductDTO();
        dto.setImage("invalid-image-url");

        StoreEntity store = new StoreEntity();
        store.setId(1L);
        when(storeRepository.findById(any())).thenReturn(Optional.of(store));
        when(categoryRepository.findAllById(any())).thenReturn(List.of(new CategoryEntity()));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            productsServices.createProduct(dto);
        });

        assertEquals("Invalid Product Image", exception.getMessage());
    }

    @Test
    void testUpdateProductQuantity_Success() {
        ProductEntity product = new ProductEntity();
        product.setId(1L);
        product.setQuantity(5);

        when(productsRepository.findById(1L)).thenReturn(Optional.of(product));

        int updated = productsServices.updateProductQuantity(1L, 3);

        assertEquals(8, updated);
        verify(productsRepository).save(product);
    }

    @Test
    void testUpdateProductQuantity_NegativeResult() {
        ProductEntity product = new ProductEntity();
        product.setId(1L);
        product.setQuantity(2);

        when(productsRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(IllegalArgumentException.class, () -> {
            productsServices.updateProductQuantity(1L, -5);
        });
    }

    @Test
    void testDeleteProduct_Success() {
        when(productsRepository.deleteProductEntitiesById(1L)).thenReturn(1);
        assertTrue(productsServices.deleteProduct(1L));
    }

    @Test
    void testGetProductById_Exists() {
        ProductEntity product = new ProductEntity();
        product.setId(1L);

        when(productsRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductEntity result = productsServices.getProductById(1L);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetProductById_NotFound() {
        when(productsRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            productsServices.getProductById(99L);
        });
    }

    @Test
    void testUpdateProduct_WithValidation() {
        ProductEntity product = new ProductEntity();
        product.setName("Product");
        product.setPrice(10.0);
        product.setQuantity(5);
        product.setBrand("Brand");
        product.setImage("data:image/png;base64,...");

        StoreEntity store = new StoreEntity();
        store.setId(1L);
        product.setStore(store);
        product.setCategories(Set.of(new CategoryEntity()));

        boolean result = productsServices.updateProduct(product);

        assertTrue(result);
        verify(productsRepository).save(product);
    }
}
