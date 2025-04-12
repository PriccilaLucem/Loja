package org.example.loja.controller;

import org.example.loja.config.security.JwtTokenProvider;
import org.example.loja.dto.LoginDTO;
import org.example.loja.entities.StoreManagerEntity;
import org.example.loja.services.StoreManagerService;
import org.example.loja.util.Authorization;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StoreManagerLoginControllerTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private StoreManagerService storeManagerService;

    @InjectMocks
    private StoreManagerLoginController storeManagerLoginController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPost_SuccessfulLogin() throws Exception {
        // Arrange
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("manager@example.com");
        loginDTO.setPassword("securePassword123");

        StoreManagerEntity storeManager = new StoreManagerEntity();
        storeManager.setEmail(loginDTO.getEmail());
        storeManager.setPassword("hashedPassword");

        when(storeManagerService.getStoreIdByStoreManagerEmail(loginDTO.getEmail()))
                .thenReturn(storeManager);

        try (MockedStatic<Authorization> mockedAuth = Mockito.mockStatic(Authorization.class)) {
            mockedAuth.when(() -> Authorization.isAuthorized(loginDTO.getPassword(), storeManager.getPassword()))
                    .thenReturn(true);

            when(jwtTokenProvider.generateStoreManagerToken(storeManager))
                    .thenReturn("generated.jwt.token");

            // Act
            ResponseEntity<?> response = storeManagerLoginController.post(loginDTO);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertInstanceOf(Map.class, response.getBody());

            @SuppressWarnings("unchecked")
            Map<String, String> responseBody = (Map<String, String>) response.getBody();
            assertEquals("generated.jwt.token", responseBody.get("token"));
        }

        verify(storeManagerService).getStoreIdByStoreManagerEmail(loginDTO.getEmail());
        verify(jwtTokenProvider).generateStoreManagerToken(storeManager);
    }

    @Test
    void testPost_InvalidEmail() {
        // Arrange
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("nonexistent@example.com");
        loginDTO.setPassword("anyPassword");

        when(storeManagerService.getStoreIdByStoreManagerEmail(loginDTO.getEmail()))
                .thenReturn(null);

        // Act
        ResponseEntity<?> response = storeManagerLoginController.post(loginDTO);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid credentials", ((Map<?, ?>) response.getBody()).get("error"));

        verify(storeManagerService).getStoreIdByStoreManagerEmail(loginDTO.getEmail());
        verifyNoInteractions(jwtTokenProvider);
    }

    @Test
    void testPost_InvalidPassword() {
        // Arrange
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("manager@example.com");
        loginDTO.setPassword("wrongPassword");

        StoreManagerEntity storeManager = new StoreManagerEntity();
        storeManager.setEmail(loginDTO.getEmail());
        storeManager.setPassword("hashedPassword");

        when(storeManagerService.getStoreIdByStoreManagerEmail(loginDTO.getEmail()))
                .thenReturn(storeManager);

        try (MockedStatic<Authorization> mockedAuth = Mockito.mockStatic(Authorization.class)) {
            mockedAuth.when(() -> Authorization.isAuthorized(loginDTO.getPassword(), storeManager.getPassword()))
                    .thenReturn(false);

            // Act
            ResponseEntity<?> response = storeManagerLoginController.post(loginDTO);

            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("Invalid credentials", ((Map<?, ?>) response.getBody()).get("error"));
        }

        verify(storeManagerService).getStoreIdByStoreManagerEmail(loginDTO.getEmail());
        verifyNoInteractions(jwtTokenProvider);
    }

    @Test
    void testPost_InternalServerError() {
        // Arrange
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("manager@example.com");
        loginDTO.setPassword("securePassword123");

        when(storeManagerService.getStoreIdByStoreManagerEmail(loginDTO.getEmail()))
                .thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<?> response = storeManagerLoginController.post(loginDTO);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("An unexpected error occurred", ((Map<?, ?>) response.getBody()).get("error"));

        verify(storeManagerService).getStoreIdByStoreManagerEmail(loginDTO.getEmail());
        verifyNoInteractions(jwtTokenProvider);
    }
}