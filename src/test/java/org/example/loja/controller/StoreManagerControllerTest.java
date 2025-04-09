package org.example.loja.controller;

import org.example.loja.config.security.JwtTokenProvider;
import org.example.loja.dto.StoreManagerDTO;
import org.example.loja.services.StoreManagerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StoreManagerControllerTest {

    @InjectMocks
    private StoreManagerController controller;

    @Mock
    private StoreManagerService service;

    private final UUID mockAdminId = UUID.randomUUID();
    private final Long mockStoreId = 1L;
    private final UUID mockStoreManagerId = UUID.randomUUID();

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Simula o comportamento do JwtTokenProvider
        mockStaticJwtTokenProvider();
    }

    private void mockStaticJwtTokenProvider() {
        mockStatic(JwtTokenProvider.class);
        when(JwtTokenProvider.extractIdFromToken(anyString())).thenReturn(mockAdminId);
    }

    @Test
    public void testPost_Success() {
        StoreManagerDTO dto = new StoreManagerDTO();
        dto.setStoreId(mockStoreId);
        dto.setName("João");
        dto.setEmail("joao@email.com");
        dto.setPassword("123456");
        dto.setCpf("39053344705");

        when(service.verifyIfIsAuthorized(mockAdminId, mockStoreId)).thenReturn(true);
        when(service.saveStoreManager(dto)).thenReturn(mockStoreManagerId);

        ResponseEntity<?> response = controller.post(dto, "Bearer token");

        assertEquals(201, response.getStatusCodeValue());
        assertTrue(((Map<?, ?>) response.getBody()).containsKey("id"));
    }

    @Test
    public void testPost_NotAuthorized() {
        StoreManagerDTO dto = new StoreManagerDTO();
        dto.setStoreId(mockStoreId);

        when(service.verifyIfIsAuthorized(mockAdminId, mockStoreId)).thenReturn(false);

        ResponseEntity<?> response = controller.post(dto, "Bearer token");

        assertEquals(403, response.getStatusCodeValue());
        assertTrue(((Map<?, ?>) response.getBody()).containsKey("error"));
    }

    @Test
    public void testPost_ThrowsException() {
        StoreManagerDTO dto = new StoreManagerDTO();
        dto.setStoreId(mockStoreId);

        when(service.verifyIfIsAuthorized(mockAdminId, mockStoreId)).thenReturn(true);
        when(service.saveStoreManager(dto)).thenThrow(new RuntimeException("Unexpected"));

        ResponseEntity<?> response = controller.post(dto, "Bearer token");

        assertEquals(500, response.getStatusCodeValue());
        assertTrue(((Map<?, ?>) response.getBody()).containsKey("error"));
    }

    @Test
    public void testPutDeactivateManager_Success() {
        when(service.verifyIfIsAuthorized(mockAdminId, mockStoreId)).thenReturn(true);
        when(service.dissociateStoreManager(mockStoreManagerId)).thenReturn(true);

        ResponseEntity<?> response = controller.putDeactivateManager(mockStoreManagerId, mockStoreId, "Bearer token");

        assertEquals(202, response.getStatusCodeValue());
        assertTrue(((Map<?, ?>) response.getBody()).get("message").equals("Store manager deactivated"));
    }

    @Test
    public void testPutActivateManager_NotFound() {
        when(service.verifyIfIsAuthorized(mockAdminId, mockStoreId)).thenReturn(true);
        when(service.activateStoreManager(mockStoreManagerId, mockStoreId)).thenReturn(false);

        ResponseEntity<?> response = controller.putActiveManager(mockStoreManagerId, mockStoreId, "Bearer token");

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testDelete_Success() {
        when(service.verifyIfIsAuthorized(mockAdminId, mockStoreId)).thenReturn(true);
        when(service.deleteStoreManager(mockStoreManagerId)).thenReturn(true);

        ResponseEntity<?> response = controller.delete(mockStoreManagerId, mockStoreId, "Bearer token");

        assertEquals(202, response.getStatusCodeValue());
        assertEquals("Store manager deleted", ((Map<?, ?>) response.getBody()).get("message"));
    }

    @Test
    public void testDelete_NotFound() {
        when(service.verifyIfIsAuthorized(mockAdminId, mockStoreId)).thenReturn(true);
        when(service.deleteStoreManager(mockStoreManagerId)).thenReturn(false);

        ResponseEntity<?> response = controller.delete(mockStoreManagerId, mockStoreId, "Bearer token");

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Store manager not found", ((Map<?, ?>) response.getBody()).get("error"));
    }

    // Mocking JwtTokenProvider as a static class
    private void mockStatic(Class<?> clazz) {
        try {
            Mockito.mockStatic(clazz).when(() -> JwtTokenProvider.extractIdFromToken(anyString())).thenReturn(mockAdminId);
        } catch (Exception ignored) {}
    }
}
