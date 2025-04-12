package org.example.loja.controller;

import org.example.loja.dto.ProductDTO;
import org.example.loja.entities.ProductEntity;
import org.example.loja.services.ProductsServices;
import org.example.loja.services.StoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductControllerTest {

    @Mock
    private ProductsServices productsServices;

    @Mock
    private StoreService storeService;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPostProduct_Success() {
        // Arrange
        Long storeId = 1L;
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("Test Product");
        productDTO.setPrice(19.99);
        productDTO.setQuantity(100);

        ProductEntity createdProduct = new ProductEntity();
        createdProduct.setId(123L);
        createdProduct.setName(productDTO.getName());
        createdProduct.setPrice(productDTO.getPrice());
        createdProduct.setQuantity(productDTO.getQuantity());

        when(productsServices.createProduct(productDTO)).thenReturn(createdProduct);
        when(storeService.addProductsToStore(storeId, createdProduct)).thenReturn(true);

        // Act
        ResponseEntity<?> response = productController.post(productDTO, storeId);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertInstanceOf(Map.class, response.getBody());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("Product created", responseBody.get("message"));
        assertEquals(123L, responseBody.get("id"));

        verify(productsServices).createProduct(productDTO);
        verify(storeService).addProductsToStore(storeId, createdProduct);
        verify(productsServices, never()).deleteProduct(anyLong());
    }

    @Test
    void testPostProduct_FailedToLinkWithStore() {
        // Arrange
        Long storeId = 1L;
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("Test Product");
        productDTO.setPrice(19.99);
        productDTO.setQuantity(100);

        ProductEntity createdProduct = new ProductEntity();
        createdProduct.setId(123L);

        when(productsServices.createProduct(productDTO)).thenReturn(createdProduct);
        when(storeService.addProductsToStore(storeId, createdProduct)).thenReturn(false);

        // Act
        ResponseEntity<?> response = productController.post(productDTO, storeId);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Product creation failed", ((Map<?, ?>) response.getBody()).get("message"));

        verify(productsServices).createProduct(productDTO);
        verify(storeService).addProductsToStore(storeId, createdProduct);
        verify(productsServices).deleteProduct(123L);
    }

    @Test
    void testPostProduct_InvalidInput() {
        // Arrange
        Long storeId = 1L;
        ProductDTO productDTO = new ProductDTO(); // Invalid - name is null
        String errorMessage = "Product name cannot be empty";

        when(productsServices.createProduct(productDTO))
                .thenThrow(new IllegalArgumentException(errorMessage));

        // Act
        ResponseEntity<?> response = productController.post(productDTO, storeId);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(errorMessage, ((Map<?, ?>) response.getBody()).get("error"));
    }

    @Test
    void testUpdateProductQuantity_Success() {
        // Arrange
        long productId = 123L;
        int quantity = 50;
        int updatedQuantity = 75;

        when(productsServices.updateProductQuantity(productId, quantity)).thenReturn(updatedQuantity);

        // Act
        ResponseEntity<?> response = productController.updateProductQuantity(productId, quantity);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("Product quantity updated", responseBody.get("message"));
        assertEquals(updatedQuantity, responseBody.get("productQuantity"));
        assertEquals(productId, responseBody.get("productId"));
    }

    @Test
    void testUpdateProductQuantity_InvalidQuantity() {
        // Arrange
        long productId = 123L;
        int quantity = -5;
        String errorMessage = "Quantity cannot be negative";

        when(productsServices.updateProductQuantity(productId, quantity))
                .thenThrow(new IllegalArgumentException(errorMessage));

        // Act
        ResponseEntity<?> response = productController.updateProductQuantity(productId, quantity);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(errorMessage, ((Map<?, ?>) response.getBody()).get("error"));
    }

    @Test
    void testDeleteProduct_Success() {
        // Arrange
        long productId = 123L;
        when(productsServices.deleteProduct(productId)).thenReturn(true);

        // Act
        ResponseEntity<?> response = productController.delete(productId);

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Product deleted", ((Map<?, ?>) response.getBody()).get("message"));
    }

    @Test
    void testDeleteProduct_NotFound() {
        // Arrange
        long productId = 123L;
        when(productsServices.deleteProduct(productId)).thenReturn(false);

        // Act
        ResponseEntity<?> response = productController.delete(productId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Product not found", ((Map<?, ?>) response.getBody()).get("error"));
    }

    @Test
    void testUpdateProduct_Success() {
        // Arrange
        long productId = 123L;
        ProductEntity product = new ProductEntity();
        product.setName("Updated Product");
        product.setPrice(29.99);
        product.setQuantity(50);

        when(productsServices.updateProduct(product)).thenReturn(true);

        // Act
        ResponseEntity<?> response = productController.updateProduct(productId, product);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Product updated successfully", ((Map<?, ?>) response.getBody()).get("message"));
        assertEquals(productId, product.getId());
    }

    @Test
    void testUpdateProduct_NotFound() {
        // Arrange
        long productId = 123L;
        ProductEntity product = new ProductEntity();
        product.setName("Updated Product");

        when(productsServices.updateProduct(product)).thenReturn(false);

        // Act
        ResponseEntity<?> response = productController.updateProduct(productId, product);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Product not found", ((Map<?, ?>) response.getBody()).get("error"));
    }
}