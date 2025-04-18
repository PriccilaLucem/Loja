package org.example.loja.controller;

import io.github.cdimascio.dotenv.Dotenv;
import org.example.loja.config.security.JwtTokenProvider;
import org.example.loja.dto.LoginDTO;
import org.example.loja.entities.StoreAdminEntity;
import org.example.loja.services.StoreAdminServices;
import org.example.loja.util.Authorization;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class    StoreAdminLoginControllerTest {

    @InjectMocks
    private StoreAdminLoginController storeAdminLoginController;

    @Mock
    private StoreAdminServices storeAdminServices;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(jwtTokenProvider.validateToken(any(String.class))).thenReturn(true);
    }
    @BeforeAll
    public static void setUpBeforeClass() throws Exception {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();
        System.setProperty("PUBLIC_KEY", dotenv.get("PUBLIC_KEY"));
        System.setProperty("PRIVATE_KEY", dotenv.get("PRIVATE_KEY"));

    }
    @Test
    void testLogin_Success() throws Exception {
        LoginDTO login = new LoginDTO();
        login.setEmail("admin@test.com");
        login.setPassword("correct_password");

        StoreAdminEntity admin = new StoreAdminEntity();
        admin.setId(UUID.randomUUID());
        admin.setEmail("admin@test.com");
        admin.setPassword("hashed_password");
        admin.setStatus(true);
        admin.setActive(true);

        when(storeAdminServices.getStoreAdminByEmail(login.getEmail())).thenReturn(admin);

        try (MockedStatic<Authorization> mockedAuth = mockStatic(Authorization.class)) {
            mockedAuth.when(() -> Authorization.isAuthorized(anyString(), anyString()))
                    .thenReturn(true);

            when(jwtTokenProvider.generateStoreAdminToken(admin))
                    .thenReturn("mock_jwt_token");

            ResponseEntity<?> response = storeAdminLoginController.login(login);
            System.out.println(response.getBody());
            assertNotNull(response);
            assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
            assertEquals(Map.of("token", "mock_jwt_token"), response.getBody());

            verify(jwtTokenProvider, times(1)).generateStoreAdminToken(admin);
        }

        verify(storeAdminServices, times(1)).getStoreAdminByEmail(login.getEmail());
    }

    @Test
    void testLogin_InvalidCredentials() {
        // Arrange
        LoginDTO login = new LoginDTO();
        login.setEmail("admin@test.com");
        login.setPassword("wrong_password");

        StoreAdminEntity admin = new StoreAdminEntity();
        admin.setId(UUID.randomUUID());
        admin.setEmail("admin@test.com");
        admin.setPassword("hashed_password");

        when(storeAdminServices.getStoreAdminByEmail(login.getEmail())).thenReturn(admin);

        try (MockedStatic<Authorization> mockedAuth = mockStatic(Authorization.class)) {
            mockedAuth.when(() -> Authorization.isAuthorized(login.getPassword(), admin.getPassword()))
                    .thenReturn(false);

            ResponseEntity<?> response = storeAdminLoginController.login(login);

            assertNotNull(response);
            assertEquals(HttpStatusCode.valueOf(401), response.getStatusCode());
            assertEquals(Map.of("error", "Invalid credentials"), response.getBody());
        }

        verify(storeAdminServices, times(1)).getStoreAdminByEmail(login.getEmail());
        verifyNoMoreInteractions(jwtTokenProvider);
    }

    @Test
    void testLogin_EmailNotFound() {
        // Arrange
        LoginDTO login = new LoginDTO();
        login.setEmail("nonexistent@test.com");
        login.setPassword("password");

        when(storeAdminServices.getStoreAdminByEmail(login.getEmail()))
                .thenThrow(new IllegalArgumentException("Store Admin not found"));

        // Act
        ResponseEntity<?> response = storeAdminLoginController.login(login);

        // Assert
        assertNotNull(response);
        assertEquals(400, response.getStatusCode().value());
        assertEquals(Map.of("error", "Store Admin not found"), response.getBody());

        verify(storeAdminServices, times(1)).getStoreAdminByEmail(login.getEmail());
        verifyNoInteractions(jwtTokenProvider);
    }

    @Test
    void testLogin_ExceptionDuringProcessing() {
        LoginDTO login = new LoginDTO();
        login.setEmail("admin@test.com");
        login.setPassword("password");

        when(storeAdminServices.getStoreAdminByEmail(login.getEmail()))
                .thenThrow(new RuntimeException("Unexpected error"));

        ResponseEntity<?> response = storeAdminLoginController.login(login);

        assertNotNull(response);
        assertEquals(400, response.getStatusCode().value());
        assertEquals(Map.of("error", "Unexpected error"), response.getBody());

        verify(storeAdminServices, times(1)).getStoreAdminByEmail(login.getEmail());
    }
}