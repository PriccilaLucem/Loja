package org.example.loja.services;

import org.example.loja.dto.StoreManagerDTO;
import org.example.loja.entities.StoreEntity;
import org.example.loja.entities.StoreManagerEntity;
import org.example.loja.repository.StoreManagerRepository;
import org.example.loja.repository.StoreRepository;
import org.example.loja.util.CPFValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StoreManagerServiceTest {

    @Mock
    private StoreManagerRepository storeManagerRepository;


    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private StoreManagerService storeManagerService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // Test for saveStoreManager
    @Test
    void shouldSaveStoreManagerSuccessfully() {
        StoreManagerDTO dto = new StoreManagerDTO();
        dto.setName("John Doe");
        dto.setEmail("john.doe@example.com");
        dto.setCpf("12345678901");
        dto.setPassword("password123");
        dto.setStoreId(1L);

        StoreManagerEntity savedManager = new StoreManagerEntity();
        try (MockedStatic<CPFValidator> cpfValidatorMock = Mockito.mockStatic(CPFValidator.class)) {
            cpfValidatorMock.when(() -> CPFValidator.isValidCPF(dto.getCpf())).thenReturn(true);
            savedManager.setId(UUID.randomUUID());
            when(storeRepository.findById(dto.getStoreId())).thenReturn(Optional.of(new StoreEntity()));
            when(storeManagerRepository.save(any(StoreManagerEntity.class))).thenReturn(savedManager);
            UUID savedId = storeManagerService.saveStoreManager(dto);

            assertNotNull(savedId);
            verify(storeRepository).findById(dto.getStoreId());
            verify(storeManagerRepository).save(any(StoreManagerEntity.class));
        }
    }


    @Test
    void shouldThrowExceptionForInvalidStoreWhileSavingManager() {
        // Arrange
        StoreManagerDTO dto = new StoreManagerDTO();
        dto.setName("John Doe");
        dto.setEmail("john.doe@example.com");
        dto.setCpf("568.002.950-27");
        dto.setPassword("password123");
        dto.setStoreId(1L);

        when(storeRepository.findById(dto.getStoreId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> storeManagerService.saveStoreManager(dto));
        verify(storeRepository).findById(dto.getStoreId());
        verify(storeManagerRepository, never()).save(any(StoreManagerEntity.class));
    }

    // Test for dissociateStoreManager
    @Test
    void shouldDissociateStoreManagerSuccessfully() {
        // Arrange
        UUID managerId = UUID.randomUUID();

        StoreManagerEntity manager = new StoreManagerEntity();
        StoreEntity store = new StoreEntity();
        manager.setStore(store);

        when(storeManagerRepository.findById(managerId)).thenReturn(Optional.of(manager));

        // Act
        boolean result = storeManagerService.dissociateStoreManager(managerId);

        // Assert
        assertTrue(result);
        assertNull(manager.getStore());
        assertFalse(manager.getStatus());
        verify(storeManagerRepository).findById(managerId);
        verify(storeRepository).save(store);
        verify(storeManagerRepository).save(manager);
    }

    @Test
    void shouldThrowExceptionWhenDissociatingNonExistingStoreManager() {
        // Arrange
        UUID managerId = UUID.randomUUID();
        when(storeManagerRepository.findById(managerId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> storeManagerService.dissociateStoreManager(managerId));
        verify(storeManagerRepository).findById(managerId);
    }

    // Test for activateStoreManager
    @Test
    void shouldActivateStoreManagerSuccessfully() {
        // Arrange
        UUID managerId = UUID.randomUUID();
        Long storeId = 1L;

        when(storeManagerRepository.updateStatusToTrue(managerId, storeId)).thenReturn(1);

        // Act
        boolean result = storeManagerService.activateStoreManager(managerId, storeId);

        // Assert
        assertTrue(result);
        verify(storeManagerRepository).updateStatusToTrue(managerId, storeId);
    }

    @Test
    void shouldFailToActivateStoreManagerWithNoAffectedRows() {
        // Arrange
        UUID managerId = UUID.randomUUID();
        Long storeId = 1L;

        when(storeManagerRepository.updateStatusToTrue(managerId, storeId)).thenReturn(0);

        // Act
        boolean result = storeManagerService.activateStoreManager(managerId, storeId);

        // Assert
        assertFalse(result);
        verify(storeManagerRepository).updateStatusToTrue(managerId, storeId);
    }

    // Test for deleteStoreManager
    @Test
    void shouldDeleteStoreManagerSuccessfully() {
        // Arrange
        UUID managerId = UUID.randomUUID();
        when(storeManagerRepository.deleteStoreManagerById(managerId)).thenReturn(1);

        // Act
        boolean result = storeManagerService.deleteStoreManager(managerId);

        // Assert
        assertTrue(result);
        verify(storeManagerRepository).deleteStoreManagerById(managerId);
    }

    @Test
    void shouldHandleFailureToDeleteStoreManager() {
        // Arrange
        UUID managerId = UUID.randomUUID();
        when(storeManagerRepository.deleteStoreManagerById(managerId)).thenReturn(0);

        // Act
        boolean result = storeManagerService.deleteStoreManager(managerId);

        // Assert
        assertFalse(result);
        verify(storeManagerRepository).deleteStoreManagerById(managerId);
    }

    // Test for verifyIfIsAuthorized
    @Test
    void shouldReturnTrueForAuthorizedManager() {
        // Arrange
        UUID managerId = UUID.randomUUID();
        Long storeId = 1L;

        StoreManagerEntity manager = new StoreManagerEntity();
        StoreEntity store = new StoreEntity();
        store.setId(storeId);
        manager.setStore(store);

        when(storeManagerRepository.findById(managerId)).thenReturn(Optional.of(manager));

        // Act
        boolean result = storeManagerService.verifyIfIsAuthorized(managerId, storeId);

        // Assert
        assertTrue(result);
        verify(storeManagerRepository).findById(managerId);
    }

    @Test
    void shouldReturnFalseForUnauthorizedManager() {
        // Arrange
        UUID managerId = UUID.randomUUID();
        Long storeId = 1L;

        StoreManagerEntity manager = new StoreManagerEntity();
        manager.setStore(null); // No association

        when(storeManagerRepository.findById(managerId)).thenReturn(Optional.of(manager));

        // Act
        boolean result = storeManagerService.verifyIfIsAuthorized(managerId, storeId);

        // Assert
        assertFalse(result);
        verify(storeManagerRepository).findById(managerId);
    }
}