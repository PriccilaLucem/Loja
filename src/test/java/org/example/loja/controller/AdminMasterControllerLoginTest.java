package org.example.loja.controller;

import org.example.loja.config.security.JwtTokenProvider;
import org.example.loja.dto.LoginDTO;
import org.example.loja.entities.AdminMasterEntity;
import org.example.loja.services.AdminMasterService;
import org.example.loja.util.Authorization;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminMasterControllerLoginTest {

    @Mock
    private AdminMasterService adminMasterService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AdminMasterControllerLogin adminMasterControllerLogin;

    private LoginDTO validLogin;
    private LoginDTO invalidLogin;
    private AdminMasterEntity adminMasterEntity;

    @BeforeEach
    void setUp() {
        validLogin = new LoginDTO();
        validLogin.setEmail("admin@example.com");
        validLogin.setPassword("correctPassword");

        invalidLogin = new LoginDTO();
        invalidLogin.setEmail("admin@example.com");
        invalidLogin.setPassword("wrongPassword");

        adminMasterEntity = new AdminMasterEntity();
        adminMasterEntity.setEmail("admin@example.com");
        adminMasterEntity.setPassword("hashedCorrectPassword");
    }

    @Test
    void login_ShouldReturnToken_WhenCredentialsAreValid() throws Exception {
        when(adminMasterService.getAdminMasterByEmail(validLogin.getEmail()))
                .thenReturn(adminMasterEntity);
        try (MockedStatic<Authorization> mockedAuth = Mockito.mockStatic(Authorization.class)) {
            mockedAuth.when(() -> Authorization.isAuthorized(validLogin.getPassword(), adminMasterEntity.getPassword()))
                    .thenReturn(true);

            String testToken = "test.jwt.token";
            when(jwtTokenProvider.generateAdminMasterToken(adminMasterEntity))
                    .thenReturn(testToken);

            ResponseEntity<?> response = adminMasterControllerLogin.login(validLogin);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(testToken, ((Map<?, ?>) response.getBody()).get("token"));

            verify(adminMasterService).getAdminMasterByEmail(validLogin.getEmail());
            verify(jwtTokenProvider).generateAdminMasterToken(adminMasterEntity);
        }
    }

    @Test
    void login_ShouldReturnUnauthorized_WhenPasswordIsInvalid() throws Exception {
        // Arrange
        when(adminMasterService.getAdminMasterByEmail(invalidLogin.getEmail()))
                .thenReturn(adminMasterEntity);
        try (MockedStatic<Authorization> mockedAuth = Mockito.mockStatic(Authorization.class)) {
            mockedAuth.when(() -> Authorization.isAuthorized(validLogin.getPassword(), adminMasterEntity.getPassword()))
                    .thenReturn(false);

            // Act
            ResponseEntity<?> response = adminMasterControllerLogin.login(invalidLogin);

            // Assert
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("Invalid credentials", ((Map<?, ?>) response.getBody()).get("error"));

            verify(adminMasterService).getAdminMasterByEmail(invalidLogin.getEmail());
            verifyNoInteractions(jwtTokenProvider);
        }
    }

    @Test
    void login_ShouldReturnUnauthorized_WhenAdminNotFound() throws Exception {
        // Arrange
        when(adminMasterService.getAdminMasterByEmail(anyString()))
                .thenThrow(new IllegalArgumentException("Admin not found"));

        // Act
        ResponseEntity<?> response = adminMasterControllerLogin.login(validLogin);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid credentials", ((Map<?, ?>) response.getBody()).get("error"));

        verify(adminMasterService).getAdminMasterByEmail(validLogin.getEmail());
        verifyNoInteractions(jwtTokenProvider);
    }

    @Test
    void login_ShouldReturnInternalServerError_WhenUnexpectedExceptionOccurs() throws Exception {
        // Arrange
        when(adminMasterService.getAdminMasterByEmail(anyString()))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Act
        ResponseEntity<?> response = adminMasterControllerLogin.login(validLogin);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("An unexpected error occurred", ((Map<?, ?>) response.getBody()).get("error"));

        verify(adminMasterService).getAdminMasterByEmail(validLogin.getEmail());
        verifyNoInteractions(jwtTokenProvider);
    }
}