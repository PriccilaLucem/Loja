package org.example.loja.services;

import org.example.loja.entities.AdminMasterEntity;
import org.example.loja.repository.AdminMasterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AdminMasterServiceTest {

    @InjectMocks
    private AdminMasterService service;

    @Mock
    private AdminMasterRepository repository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAdminMasterByEmail_Success() {
        String email = "admin@example.com";
        AdminMasterEntity mockAdmin = new AdminMasterEntity();
        mockAdmin.setId(UUID.randomUUID());
        mockAdmin.setEmail(email);

        when(repository.findByEmail(email)).thenReturn(Optional.of(mockAdmin));

        AdminMasterEntity result = service.getAdminMasterByEmail(email);

        assertNotNull(result);
        assertEquals(email, result.getEmail());
    }

    @Test
    public void testGetAdminMasterByEmail_NotFound() {
        String email = "admin@example.com";

        when(repository.findByEmail(email)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.getAdminMasterByEmail(email);
        });

        assertEquals("Invalid Credentials", exception.getMessage());
    }
}
