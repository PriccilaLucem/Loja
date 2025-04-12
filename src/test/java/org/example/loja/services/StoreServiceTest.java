package org.example.loja.services;

import org.example.loja.entities.AddressEntity;
import org.example.loja.entities.StoreAdminEntity;
import org.example.loja.entities.StoreEntity;
import org.example.loja.repository.StoreAdminRepository;
import org.example.loja.repository.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StoreServiceTest {

    private StoreService storeService;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private StoreAdminRepository storeAdminRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        storeService = new StoreService();
        storeService.storeRepository = storeRepository;
        storeService.storeAdminRepository = storeAdminRepository;
    }

    @Test
    void saveStore_ShouldThrowException_WhenStoreAdminIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> storeService.saveStore(new StoreEntity(), null));
        assertEquals("Invalid Credentials", exception.getMessage());
    }

    @Test
    void saveStore_ShouldThrowException_WhenStoreAdminDoesNotExist() {
        UUID adminUuid = UUID.randomUUID();

        when(storeAdminRepository.findById(adminUuid)).thenReturn(Optional.empty());

        StoreEntity store = new StoreEntity();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> storeService.saveStore(store, adminUuid));
        assertEquals("Invalid Credentials", exception.getMessage());
    }

    @Test
    void saveStore_ShouldReturnId_WhenStoreIsValid() {
        UUID adminUuid = UUID.randomUUID();
        StoreAdminEntity adminEntity = new StoreAdminEntity();
        adminEntity.setId(adminUuid);

        StoreEntity store = new StoreEntity();
        store.setName("Test Store");
        store.setDescription("Test Description");
        store.setEmail("test@store.com");
        store.setPhone("+14155552671");

        store.setLocations(Set.of(new AddressEntity()));
        store.setStoreAdmin(adminEntity); // <- ESSA LINHA É ESSENCIAL

        when(storeAdminRepository.findById(adminUuid)).thenReturn(Optional.of(adminEntity));

        StoreEntity savedStore = new StoreEntity();
        savedStore.setId(1L);
        when(storeRepository.save(any(StoreEntity.class))).thenReturn(savedStore);

        Long result = storeService.saveStore(store, adminUuid);

        assertNotNull(result);
        assertEquals(1L, result);
    }


    @Test
    void deleteStore_ShouldThrowException_WhenIdIsInvalid() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> storeService.deleteStore(-1L));

        assertEquals("Store ID must be a valid positive number", exception.getMessage());
    }

    @Test
    void deleteStore_ShouldReturnTrue_WhenStoreIsDeactivated() {
        Long storeId = 1L;

        when(storeRepository.deactivateStoreById(storeId)).thenReturn(1);

        boolean result = storeService.deleteStore(storeId);

        assertTrue(result);
    }

    @Test
    void deleteStore_ShouldReturnFalse_WhenNoStoreIsDeactivated() {
        Long storeId = 1L;

        when(storeRepository.deactivateStoreById(storeId)).thenReturn(0);

        boolean result = storeService.deleteStore(storeId);

        assertFalse(result);
    }

    @Test
    void verifyIfIsAuthorized_ShouldThrowException_WhenUuidOrIdIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> storeService.verifyIfIsAuthorized(null, 1L));

        assertEquals("UUID or Store ID cannot be null", exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class,
                () -> storeService.verifyIfIsAuthorized(UUID.randomUUID(), null));

        assertEquals("UUID or Store ID cannot be null", exception.getMessage());
    }

    @Test
    void verifyIfIsAuthorized_ShouldThrowException_WhenAdminDoesNotExist() {
        UUID uuid = UUID.randomUUID();
        when(storeAdminRepository.findById(uuid)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> storeService.verifyIfIsAuthorized(uuid, 1L));

        assertEquals("Invalid Credentials", exception.getMessage());
    }

    @Test
    void verifyIfIsAuthorized_ShouldReturnFalse_WhenAdminIsNotAuthorizedForStore() {
        UUID uuid = UUID.randomUUID();
        StoreAdminEntity adminEntity = new StoreAdminEntity();
        when(storeAdminRepository.findById(uuid)).thenReturn(Optional.of(adminEntity));

        boolean result = storeService.verifyIfIsAuthorized(uuid, 1L);

        assertFalse(result);
    }

    @Test
    void verifyIfIsAuthorized_ShouldReturnTrue_WhenAdminIsAuthorizedForStore() {
        UUID uuid = UUID.randomUUID();
        StoreAdminEntity adminEntity = new StoreAdminEntity();
        StoreEntity store = new StoreEntity();
        store.setId(1L);

        adminEntity.setManagedStore(java.util.Set.of(store));
        when(storeAdminRepository.findById(uuid)).thenReturn(Optional.of(adminEntity));

        boolean result = storeService.verifyIfIsAuthorized(uuid, 1L);

        assertTrue(result);
    }
}