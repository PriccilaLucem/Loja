package org.example.loja.services;

import org.example.loja.entities.AdminLogsEntity;
import org.example.loja.entities.StoreAdminEntity;
import org.example.loja.repository.StoreLogsRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminLogsServiceTest {

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

        String action = "TEST_ACTION";
        String description = "Test description";
        double lat = 40.7128;
        double lon = -74.0060;

        adminLogsService.saveLogAction(mockAdmin, action, description, lat, lon);

        ArgumentCaptor<AdminLogsEntity> logsCaptor = ArgumentCaptor.forClass(AdminLogsEntity.class);
        verify(storeLogsRepository, times(1)).save(logsCaptor.capture());

        AdminLogsEntity capturedLog = logsCaptor.getValue();
        assertNotNull(capturedLog);
        assertEquals(mockAdmin, capturedLog.getStoreAdmin());
        assertEquals(action, capturedLog.getAction());
        assertEquals(description, capturedLog.getDescription());
        assertEquals(lat, capturedLog.getLat());
        assertEquals(lon, capturedLog.getLon());
        assertNotNull(capturedLog.getTimestamp());
        assertTrue(capturedLog.getTimestamp().isBefore(LocalDateTime.now()));
    }
}