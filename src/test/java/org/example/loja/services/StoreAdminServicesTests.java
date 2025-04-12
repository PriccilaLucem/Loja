package org.example.loja.services;

import org.example.loja.entities.StoreAdminEntity;
import org.example.loja.repository.StoreAdminRepository;
import org.example.loja.util.Authorization;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoreAdminServicesTests {

    @InjectMocks
    private StoreAdminServices storeAdminServices;

    @Mock
    private StoreAdminRepository storeAdminRepository;

    @Mock
    private AdminLogsService adminLogsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllStoreAdmins() {
        when(storeAdminRepository.findAll()).thenReturn(List.of());

        var admins = storeAdminServices.getAllStoreAdmins();

        assertNotNull(admins);
        assertTrue(admins.isEmpty());
        verify(storeAdminRepository, times(1)).findAll();
    }

    @Test
    void testSaveStoreAdmin() {
        StoreAdminEntity storeAdmin = new StoreAdminEntity();
        storeAdmin.setName("Admin Test");
        storeAdmin.setEmail("admin@test.com");
        storeAdmin.setPassword("password");

        StoreAdminEntity savedAdmin = new StoreAdminEntity();
        savedAdmin.setId(UUID.randomUUID());

        when(storeAdminRepository.save(any(StoreAdminEntity.class))).thenReturn(savedAdmin);

        try (MockedStatic<Authorization> mocked = mockStatic(Authorization.class)) {
            String hashedPassword = "hashed_password";
            mocked.when(() -> Authorization.hashPassword("password")).thenReturn(hashedPassword);

            UUID resultId = storeAdminServices.saveStoreAdmin(storeAdmin, 10.0, 20.0);

            assertNotNull(resultId);
            ArgumentCaptor<StoreAdminEntity> captor = ArgumentCaptor.forClass(StoreAdminEntity.class);
            verify(storeAdminRepository, times(1)).save(captor.capture());
            StoreAdminEntity capturedAdmin = captor.getValue();

            assertEquals("Admin Test", capturedAdmin.getName());
            assertEquals("admin@test.com", capturedAdmin.getEmail());
            assertEquals(hashedPassword, capturedAdmin.getPassword());
            assertFalse(capturedAdmin.getActive());
            assertTrue(capturedAdmin.getStatus());

            verify(adminLogsService, times(1)).saveLogAction(eq(savedAdmin), eq("Created"), eq("New Store Admin created"), eq(10.0), eq(20.0));
        }
    }

    @Test
    void testGetStoreAdminById() {
        UUID adminId = UUID.randomUUID();
        StoreAdminEntity storeAdmin = new StoreAdminEntity();
        storeAdmin.setId(adminId);

        when(storeAdminRepository.findById(adminId)).thenReturn(Optional.of(storeAdmin));

        StoreAdminEntity result = storeAdminServices.getStoreAdminById(adminId);

        assertNotNull(result);
        assertEquals(adminId, result.getId());
        verify(storeAdminRepository, times(1)).findById(adminId);
    }

    @Test
    void testGetStoreAdminById_NotFound() {
        UUID adminId = UUID.randomUUID();

        when(storeAdminRepository.findById(adminId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            storeAdminServices.getStoreAdminById(adminId);
        });

        assertEquals("Store Admin not found", exception.getMessage());
        verify(storeAdminRepository, times(1)).findById(adminId);
    }

    @Test
    void testDeleteStoreAdmin() {
        UUID adminId = UUID.randomUUID();

        when(storeAdminRepository.updateStatus(adminId, false)).thenReturn(1);

        boolean result = storeAdminServices.deleteStoreAdmin(adminId);

        assertTrue(result);
        verify(storeAdminRepository, times(1)).updateStatus(adminId, false);
    }

    @Test
    void testDeleteStoreAdmin_Failed() {
        UUID adminId = UUID.randomUUID();

        when(storeAdminRepository.updateStatus(adminId, false)).thenReturn(0);

        boolean result = storeAdminServices.deleteStoreAdmin(adminId);

        assertFalse(result);
        verify(storeAdminRepository, times(1)).updateStatus(adminId, false);
    }

    @Test
    void testGetStoreAdminByEmail() {
        String email = "admin@test.com";
        StoreAdminEntity storeAdmin = new StoreAdminEntity();
        storeAdmin.setEmail(email);

        when(storeAdminRepository.findByEmail(email)).thenReturn(Optional.of(storeAdmin));

        StoreAdminEntity result = storeAdminServices.getStoreAdminByEmail(email);

        assertNotNull(result);
        assertEquals(email, result.getEmail());
        verify(storeAdminRepository, times(1)).findByEmail(email);
    }

    @Test
    void testValidateStoreAdmin_InvalidStoreAdmin() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            storeAdminServices.saveStoreAdmin(null, 0.0, 0.0);
        });

        assertEquals("Store Admin is null", exception.getMessage());
    }
}
