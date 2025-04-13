package org.example.loja.controller;

import org.example.loja.entities.ImageEntity;
import org.example.loja.services.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ImageControllerTest {

    @Mock
    private ImageService imageService;

    @InjectMocks
    private ImageController imageController;

    private MultipartFile testFile;

    @BeforeEach
    void setUp() {
        testFile = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );
    }

    @Test
    void uploadImageForProduct_Success() throws IOException {
        // Arrange
        String expectedUrl = "/uploads/store1/test.jpg";
        when(imageService.saveProductImage(any(ImageEntity.class), anyLong(), anyString()))
                .thenReturn(expectedUrl);

        // Act
        ResponseEntity<?> response = imageController.uploadImageForProduct(
                1L, testFile, "store1");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Image uploaded successfully", body.get("response"));
        assertEquals(expectedUrl, body.get("url"));

        verify(imageService, times(1)).saveProductImage(any(ImageEntity.class), eq(1L), eq("store1"));
    }

    @Test
    void uploadImageForProduct_IOException() throws IOException {
        // Arrange
        when(imageService.saveProductImage(any(ImageEntity.class), anyLong(), anyString()))
                .thenThrow(new IOException("Failed to save image"));

        // Act
        ResponseEntity<?> response = imageController.uploadImageForProduct(
                1L, testFile, "store1");

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Image upload failed", body.get("response"));
    }

    @Test
    void uploadImageForProduct_ProductNotFound() {
        // Arrange
        when(imageService.saveProductImage(any(ImageEntity.class), anyLong(), anyString()))
                .thenThrow(new RuntimeException("Product not found"));

        // Act
        ResponseEntity<?> response = imageController.uploadImageForProduct(
                1L, testFile, "store1");

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Product not found", body.get("response"));
    }

    @Test
    void uploadImageForStore_Success() throws IOException {
        // Arrange
        String expectedUrl = "/uploads/store1/store_logo.jpg";
        when(imageService.saveStoreImage(any(ImageEntity.class), anyString()))
                .thenReturn(expectedUrl);

        // Act
        ResponseEntity<?> response = imageController.uploadImageForStore(
                "store1", testFile);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Image uploaded successfully", body.get("response"));
        assertEquals(expectedUrl, body.get("url"));

        verify(imageService, times(1)).saveStoreImage(any(ImageEntity.class), eq("store1"));
    }

    @Test
    void uploadImageForStore_IOException() throws IOException {
        // Arrange
        when(imageService.saveStoreImage(any(ImageEntity.class), anyString()))
                .thenThrow(new IOException("Failed to save image"));

        // Act
        ResponseEntity<?> response = imageController.uploadImageForStore(
                "store1", testFile);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Image upload failed", body.get("response"));
    }

    @Test
    void uploadImageForStore_StoreNotFound() {
        // Arrange
        when(imageService.saveStoreImage(any(ImageEntity.class), anyString()))
                .thenThrow(new RuntimeException("Store not found"));

        // Act
        ResponseEntity<?> response = imageController.uploadImageForStore(
                "nonexistent", testFile);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Store not found", body.get("response"));
    }
}