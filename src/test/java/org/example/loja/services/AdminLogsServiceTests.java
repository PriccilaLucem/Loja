package org.example.loja.services;

import org.example.loja.entities.AdminLogsEntity;
import org.example.loja.entities.RoleEntity;
import org.example.loja.entities.StoreAdminEntity;
import org.example.loja.repository.StoreLogsRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminLogsServiceTests {

    private static AdminLogsService adminLogsService;

    @Mock
    private static StoreLogsRepository storeLogsRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        adminLogsService = new AdminLogsService();
        adminLogsService.logsRepository = storeLogsRepository;
    }

    @AfterAll
    static void tearDown() {
        verifyNoMoreInteractions(storeLogsRepository);
    }

    @Test
    void testSaveLogAction() {
        StoreAdminEntity mockAdmin = new StoreAdminEntity();
        mockAdmin.setId(UUID.randomUUID());
        mockAdmin.setName("Admin Test");
        mockAdmin.setEmail("admin@test.com");
        RoleEntity role = new RoleEntity();
        role.setName("ROLE_ADMIN");
        mockAdmin.setRole(new HashSet<>(Set.of(role)));
        String action = "TEST_ACTION";
        String description = "Test description";
        double lat = 40.7128;
        double lon = -74.0060;
        adminLogsService.saveLogAction(mockAdmin, action, description, lat, lon);

        ArgumentCaptor<AdminLogsEntity> logsCaptor = ArgumentCaptor.forClass(AdminLogsEntity.class);
        verify(storeLogsRepository, times(1)).save(logsCaptor.capture());

        AdminLogsEntity capturedLog = logsCaptor.getValue();
        assertNotNull(capturedLog);
        assertEquals(action, capturedLog.getAction());
        assertEquals(description, capturedLog.getDescription());
        assertEquals(lat, capturedLog.getLat());
        assertEquals(lon, capturedLog.getLon());
        assertNotNull(capturedLog.getTimestamp());
        assertTrue(capturedLog.getTimestamp().isBefore(LocalDateTime.now()));
    }
}