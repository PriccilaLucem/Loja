package org.example.loja.controller;

import org.example.loja.entities.StoreAdminEntity;
import org.example.loja.services.StoreAdminServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StoreAdminControllerTest {

    @InjectMocks
    private StoreAdminController storeAdminController;

    @Mock
    private StoreAdminServices storeAdminServices;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllStoreAdmins() {
        StoreAdminEntity admin1 = new StoreAdminEntity();
        admin1.setId(UUID.randomUUID());
        admin1.setName("Admin 1");

        StoreAdminEntity admin2 = new StoreAdminEntity();
        admin2.setId(UUID.randomUUID());
        admin2.setName("Admin 2");

        when(storeAdminServices.getAllStoreAdmins()).thenReturn(List.of(admin1, admin2));

        ResponseEntity<List<StoreAdminEntity>> response = storeAdminController.getAllStoreAdmins();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        verify(storeAdminServices, times(1)).getAllStoreAdmins();
    }

    @Test
    void testGetAllStoreAdmins_EmptyList() {
        when(storeAdminServices.getAllStoreAdmins()).thenReturn(List.of());

        ResponseEntity<List<StoreAdminEntity>> response = storeAdminController.getAllStoreAdmins();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void testGetStoreAdminById_Success() {
        UUID adminId = UUID.randomUUID();
        StoreAdminEntity admin = new StoreAdminEntity();
        admin.setId(adminId);
        admin.setName("Admin Test");
        admin.setEmail("admin@test.com");

        when(storeAdminServices.getStoreAdminById(adminId)).thenReturn(admin);

        ResponseEntity<?> response = storeAdminController.getStoreAdminById(adminId);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(admin, response.getBody());
        verify(storeAdminServices, times(1)).getStoreAdminById(adminId);
    }

    @Test
    void testGetStoreAdminById_NotFound() {
        UUID adminId = UUID.randomUUID();
        when(storeAdminServices.getStoreAdminById(adminId)).thenThrow(new IllegalArgumentException("Store Admin not found"));

        ResponseEntity<?> response = storeAdminController.getStoreAdminById(adminId);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
        assertEquals(Map.of("error", "Store Admin not found."), response.getBody());
        verify(storeAdminServices, times(1)).getStoreAdminById(adminId);
    }

    @Test
    void testSaveStoreAdmin_Success() {
        StoreAdminEntity admin = new StoreAdminEntity();
        admin.setName("New Admin");
        admin.setEmail("new_admin@test.com");
        admin.setPassword("password");

        UUID generatedId = UUID.randomUUID();

        when(storeAdminServices.saveStoreAdmin(admin, 37.7749, -122.4194)).thenReturn(generatedId);

        ResponseEntity<?> response = storeAdminController.saveStoreAdmin(admin, 37.7749, -122.4194);

        assertNotNull(response);
        assertEquals(201, response.getStatusCodeValue());
        assertEquals(Map.of("message", "Store Admin created.", "id", generatedId), response.getBody());
        verify(storeAdminServices, times(1)).saveStoreAdmin(admin, 37.7749, -122.4194);
    }

    @Test
    void testSaveStoreAdmin_Failure() {
        StoreAdminEntity admin = new StoreAdminEntity();
        admin.setName("Invalid Admin");
        admin.setEmail("");

        when(storeAdminServices.saveStoreAdmin(admin, 37.7749, -122.4194))
                .thenThrow(new IllegalArgumentException("Email is invalid"));

        ResponseEntity<?> response = storeAdminController.saveStoreAdmin(admin, 37.7749, -122.4194);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals(Map.of("error", "Email is invalid"), response.getBody());
        verify(storeAdminServices, times(1)).saveStoreAdmin(admin, 37.7749, -122.4194);
    }

    @Test
    void testDeleteStoreAdmin_Success() {
        UUID adminId = UUID.randomUUID();
        when(storeAdminServices.deleteStoreAdmin(adminId)).thenReturn(true);

        ResponseEntity<?> response = storeAdminController.deleteStoreAdmin(adminId);

        assertNotNull(response);
        assertEquals(204, response.getStatusCodeValue()); // No Content
        assertNull(response.getBody());
        verify(storeAdminServices, times(1)).deleteStoreAdmin(adminId);
    }

    @Test
    void testDeleteStoreAdmin_AlreadyDeactivated() {
        UUID adminId = UUID.randomUUID();
        when(storeAdminServices.deleteStoreAdmin(adminId)).thenReturn(false);

        ResponseEntity<?> response = storeAdminController.deleteStoreAdmin(adminId);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue()); // Bad Request
        assertEquals(Map.of("error", "Admin Already Deactivated"), response.getBody());
        verify(storeAdminServices, times(1)).deleteStoreAdmin(adminId);
    }

    @Test
    void testDeleteStoreAdmin_NotFound() {
        UUID adminId = UUID.randomUUID();
        when(storeAdminServices.deleteStoreAdmin(adminId)).thenThrow(new IllegalArgumentException("Store Admin not found"));

        ResponseEntity<?> response = storeAdminController.deleteStoreAdmin(adminId);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue()); // Not Found
        assertEquals(Map.of("error", "Store Admin not found."), response.getBody());
        verify(storeAdminServices, times(1)).deleteStoreAdmin(adminId);
    }
}