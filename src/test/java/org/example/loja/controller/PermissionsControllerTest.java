package org.example.loja.controller;

import org.example.loja.entities.PermissionEntity;
import org.example.loja.services.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PermissionsControllerTest {
    static class PermissionEntityBuilder {
        private Long id;
        private String name = "DEFAULT_PERM";

        public PermissionEntityBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public PermissionEntityBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public PermissionEntity build() {
            PermissionEntity permission = new PermissionEntity();
            permission.setId(id);
            permission.setName(name);
            String description = "Default description";
            permission.setDescription(description);
            return permission;
        }
    }
    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private Permissions permissionsController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Inject the mocked logger
    }

    @Test
    void testGetPermission_Success() {
        PermissionEntity permission1 = new PermissionEntityBuilder().withId(1L).withName("").build();
        PermissionEntity permission2 = new PermissionEntity();
        List<PermissionEntity> mockPermissions = Arrays.asList(permission1, permission2);

        when(employeeService.getPermissions()).thenReturn(mockPermissions);

        ResponseEntity<?> response = permissionsController.getPermission();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertInstanceOf(List.class, response.getBody());

        @SuppressWarnings("unchecked")
        List<PermissionEntity> responsePermissions = (List<PermissionEntity>) response.getBody();
        assertEquals(2, responsePermissions.size());
        assertEquals(1L, responsePermissions.get(0).getId());

        verify(employeeService).getPermissions();
    }

    @Test
    void testGetPermission_EmptyList() {
        // Arrange
        when(employeeService.getPermissions()).thenReturn(List.of());

        // Act
        ResponseEntity<?> response = permissionsController.getPermission();

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertInstanceOf(List.class, response.getBody());
        assertTrue(((List<?>) response.getBody()).isEmpty());

        verify(employeeService).getPermissions();
    }

    @Test
    void testGetPermission_ServiceThrowsException() {
        RuntimeException expectedException = new RuntimeException("Database connection failed");
        when(employeeService.getPermissions()).thenThrow(expectedException);

        ResponseEntity<?> response = permissionsController.getPermission();

        assertEquals(500, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertInstanceOf(Map.class, response.getBody());

        @SuppressWarnings("unchecked")
        Map<String, String> errorResponse = (Map<String, String>) response.getBody();
        assertEquals("An unexpected error occurred", errorResponse.get("error"));

        verify(employeeService).getPermissions();
    }
}