package org.example.loja.controller;

import org.example.loja.entities.StoreEntity;
import org.example.loja.services.StoreService;
import org.example.loja.config.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class StoreControllerTest {

    @InjectMocks
    private StoreController storeController;


    @Mock
    private StoreService storeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @WithMockUser
    void postStore_shouldCreateNewStoreWhenValidInput() throws Exception {
        StoreEntity store = new StoreEntity();
        store.setName("Test Store");
        store.setDescription("A test store");
        store.setPhone("+123456789");
        store.setEmail("test@example.com");
        UUID storeAdminId = UUID.randomUUID();
        String token = "Bearer header.payload.signature";

        try (MockedStatic<JwtTokenProvider> mockedJwt = Mockito.mockStatic(JwtTokenProvider.class)) {
            mockedJwt.when(() -> JwtTokenProvider.extractIdFromToken(anyString()))
                    .thenReturn(storeAdminId);
            when(storeService.saveStore(any(StoreEntity.class), eq(storeAdminId)))
                    .thenReturn(1L);

            ResponseEntity<?> response = storeController.postStore(store, token);

            assertNotNull(response);
            assertEquals(201, response.getStatusCode().value());

            @SuppressWarnings("unchecked")
            Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
            assertEquals("Store created", responseBody.get("message"));
            assertEquals(1L, responseBody.get("id"));

            verify(storeService, times(1)).saveStore(any(StoreEntity.class), eq(storeAdminId));
        }
    }

    @Test
    @WithMockUser
    void putStore_shouldUpdateStoreWhenAuthorized() throws Exception {
        StoreEntity store = new StoreEntity();
        store.setName("Updated Store");
        store.setDescription("Updated description");
        store.setPhone("+987654321");
        store.setEmail("updated@example.com");

        UUID storeAdminId = UUID.randomUUID();
        String token = "Bearer header.payload.signature";

        try (MockedStatic<JwtTokenProvider> mockedJwt = Mockito.mockStatic(JwtTokenProvider.class)) {
            mockedJwt.when(() -> JwtTokenProvider.extractIdFromToken(anyString()))
                    .thenReturn(storeAdminId);
            when(storeService.verifyIfIsAuthorized(eq(storeAdminId), eq(1L))).thenReturn(false);
            when(storeService.updateStore(any(StoreEntity.class))).thenReturn(true);

            ResponseEntity<?> response = storeController.putStore(store, token, 1L);

            assertNotNull(response);
            assertEquals(202, response.getStatusCode().value());

            @SuppressWarnings("unchecked")
            Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
            assertEquals("Store updated", responseBody.get("message"));

            verify(storeService, times(1)).updateStore(any(StoreEntity.class));
        }
    }

    @Test
    @WithMockUser
    void putStore_shouldReturnForbiddenWhenNotAuthorized() throws Exception {
        StoreEntity store = new StoreEntity();
        store.setName("Unauthorized Update");

        UUID storeAdminId = UUID.randomUUID();
        String token = "Bearer header.payload.signature";

        try (MockedStatic<JwtTokenProvider> mockedJwt = Mockito.mockStatic(JwtTokenProvider.class)) {
            mockedJwt.when(() -> JwtTokenProvider.extractIdFromToken(anyString()))
                    .thenReturn(storeAdminId);
            when(storeService.verifyIfIsAuthorized(eq(storeAdminId), eq(1L))).thenReturn(true);

            ResponseEntity<?> response = storeController.putStore(store, token, 1L);

            assertNotNull(response);
            assertEquals(403, response.getStatusCode().value());

            @SuppressWarnings("unchecked")
            Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
            assertEquals("You are not authorized to update this store", responseBody.get("error"));

            verify(storeService, never()).updateStore(any(StoreEntity.class));
        }
    }

    @Test
    @WithMockUser
    void deleteStore_shouldDeactivateStoreWhenAuthorized() throws Exception {
        UUID storeAdminId = UUID.randomUUID();
        String token = "Bearer header.payload.signature";

        try (MockedStatic<JwtTokenProvider> mockedJwt = Mockito.mockStatic(JwtTokenProvider.class)) {
            mockedJwt.when(() -> JwtTokenProvider.extractIdFromToken(anyString()))
                    .thenReturn(storeAdminId);
            when(storeService.verifyIfIsAuthorized(eq(storeAdminId), eq(1L))).thenReturn(false);
            when(storeService.deleteStore(eq(1L))).thenReturn(true);

            ResponseEntity<?> response = storeController.deactivateStore(1L, token);

            assertNotNull(response);
            assertEquals(202, response.getStatusCode().value());

            @SuppressWarnings("unchecked")
            Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
            assertEquals("Store deactivated", responseBody.get("message"));

            verify(storeService, times(1)).deleteStore(eq(1L));
        }
    }

    @Test
    @WithMockUser
    void deleteStore_shouldReturnForbiddenWhenNotAuthorized() throws Exception {
        UUID storeAdminId = UUID.randomUUID();
        String token = "Bearer header.payload.signature";

        try (MockedStatic<JwtTokenProvider> mockedJwt = Mockito.mockStatic(JwtTokenProvider.class)) {
            mockedJwt.when(() -> JwtTokenProvider.extractIdFromToken(anyString()))
                    .thenReturn(storeAdminId);
            when(storeService.verifyIfIsAuthorized(eq(storeAdminId), eq(1L))).thenReturn(true);

            ResponseEntity<?> response = storeController.deactivateStore(1L, token);

            assertNotNull(response);
            assertEquals(403, response.getStatusCode().value());

            @SuppressWarnings("unchecked")
            Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
            assertEquals("You are not authorized to deactivate this store", responseBody.get("error"));

            verify(storeService, never()).deleteStore(eq(1L));
        }
    }

    @Test
    @WithMockUser
    void deleteStore_shouldReturnNotFoundWhenStoreDoesNotExist() throws Exception {
        UUID storeAdminId = UUID.randomUUID();
        String token = "Bearer header.payload.signature";

        try (MockedStatic<JwtTokenProvider> mockedJwt = Mockito.mockStatic(JwtTokenProvider.class)) {
            mockedJwt.when(() -> JwtTokenProvider.extractIdFromToken(anyString()))
                    .thenReturn(storeAdminId);
            when(storeService.verifyIfIsAuthorized(eq(storeAdminId), eq(1L))).thenReturn(false);
            when(storeService.deleteStore(eq(1L))).thenReturn(false);

            ResponseEntity<?> response = storeController.deactivateStore(1L, token);

            assertNotNull(response);
            assertEquals(404, response.getStatusCode().value());

            @SuppressWarnings("unchecked")
            Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
            assertEquals("Store not found", responseBody.get("error"));

            verify(storeService, times(1)).deleteStore(eq(1L));
        }
    }
}