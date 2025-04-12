package org.example.loja.controller;

import org.example.loja.entities.EmployeeEntity;
import org.example.loja.services.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmployeeControllerTest {

    @InjectMocks
    private EmployeeController employeeController;

    @Mock
    private EmployeeService employeeService;

    private EmployeeEntity employee;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        employee = new EmployeeEntity();
        employee.setId(UUID.randomUUID());
        employee.setName("Test Employee");
    }

    @Test
    void getAllEmployees_ShouldReturnListOfEmployees() {
        List<EmployeeEntity> employees = List.of(employee);
        when(employeeService.getAllEmployees()).thenReturn(employees);

        ResponseEntity<?> response = employeeController.getAllEmployees();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(employees, response.getBody());
        verify(employeeService).getAllEmployees();
    }

    @Test
    void postEmployee_ShouldReturnCreatedEmployeeId() {
        UUID id = UUID.randomUUID();
        when(employeeService.saveEmployee(employee)).thenReturn(id);

        ResponseEntity<?> response = employeeController.postEmployee(employee);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assert body != null;
        assertEquals("EmployeeCreated", body.get("message"));
        assertEquals(id, body.get("id"));
    }

    @Test
    void postEmployee_ShouldReturnBadRequest_WhenIdIsNull() {
        when(employeeService.saveEmployee(employee)).thenReturn(null);

        ResponseEntity<?> response = employeeController.postEmployee(employee);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Employee not created", ((Map<?, ?>) Objects.requireNonNull(response.getBody())).get("error"));
    }

    @Test
    void deleteEmployee_ShouldReturnNoContent_WhenDeleted() {
        UUID id = UUID.randomUUID();
        when(employeeService.deleteEmployee(id)).thenReturn(true);

        ResponseEntity<?> response = employeeController.deleteEmployee(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void deleteEmployee_ShouldReturnNotFound_WhenNotDeleted() {
        UUID id = UUID.randomUUID();
        when(employeeService.deleteEmployee(id)).thenReturn(false);

        ResponseEntity<?> response = employeeController.deleteEmployee(id);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Employee not found", ((Map<?, ?>) Objects.requireNonNull(response.getBody())).get("error"));
    }

    @Test
    void putEmployeePermission_ShouldReturnOk_WhenPermissionAdded() {
        UUID employeeId = UUID.randomUUID();
        long permissionId = 1L;

        when(employeeService.addPermissionToEmployee(employeeId, permissionId)).thenReturn(true);

        ResponseEntity<?> response = employeeController.putEmployeePermission(employeeId, permissionId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Permission added to employee", ((Map<?, ?>) Objects.requireNonNull(response.getBody())).get("message"));
    }

    @Test
    void putEmployeePermission_ShouldReturnNotFound_WhenEmployeeMissing() {
        UUID employeeId = UUID.randomUUID();
        long permissionId = 1L;

        when(employeeService.addPermissionToEmployee(employeeId, permissionId)).thenReturn(false);

        ResponseEntity<?> response = employeeController.putEmployeePermission(employeeId, permissionId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Employee not found", ((Map<?, ?>) Objects.requireNonNull(response.getBody())).get("error"));
    }

    @Test
    void putEmployeePermission_ShouldReturnBadRequest_WhenIllegalArgument() {
        UUID employeeId = UUID.randomUUID();
        long permissionId = 1L;

        when(employeeService.addPermissionToEmployee(employeeId, permissionId))
                .thenThrow(new IllegalArgumentException("Invalid ID"));

        ResponseEntity<?> response = employeeController.putEmployeePermission(employeeId, permissionId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid ID", ((Map<?, ?>) Objects.requireNonNull(response.getBody())).get("error"));
    }

    @Test
    void removeEmployeePermission_ShouldReturnOk_WhenPermissionRemoved() {
        UUID employeeId = UUID.randomUUID();
        long permissionId = 1L;

        when(employeeService.removePermissionFromEmployee(employeeId, permissionId)).thenReturn(true);

        ResponseEntity<?> response = employeeController.removeEmployeePermission(employeeId, permissionId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Permission removed from employee", ((Map<?, ?>) Objects.requireNonNull(response.getBody())).get("message"));
    }

    @Test
    void removeEmployeePermission_ShouldReturnNotFound_WhenEmployeeMissing() {
        UUID employeeId = UUID.randomUUID();
        long permissionId = 1L;

        when(employeeService.removePermissionFromEmployee(employeeId, permissionId)).thenReturn(false);

        ResponseEntity<?> response = employeeController.removeEmployeePermission(employeeId, permissionId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Employee not found", ((Map<?, ?>) Objects.requireNonNull(response.getBody())).get("error"));
    }

    @Test
    void removeEmployeePermission_ShouldReturnBadRequest_WhenIllegalArgument() {
        UUID employeeId = UUID.randomUUID();
        long permissionId = 1L;

        when(employeeService.removePermissionFromEmployee(employeeId, permissionId))
                .thenThrow(new IllegalArgumentException("Invalid permission"));

        ResponseEntity<?> response = employeeController.removeEmployeePermission(employeeId, permissionId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid permission", ((Map<?, ?>) Objects.requireNonNull(response.getBody())).get("error"));
    }
}
