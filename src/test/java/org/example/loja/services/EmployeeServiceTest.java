package org.example.loja.services;

import org.example.loja.entities.EmployeeEntity;
import org.example.loja.entities.PermissionEntity;
import org.example.loja.repository.EmployeeRepository;
import org.example.loja.repository.PermissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EmployeeServiceTest {

    @InjectMocks
    private EmployeeService service;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private PermissionRepository permissionRepository;

    private final UUID employeeId = UUID.randomUUID();
    private final long permissionId = 1L;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllEmployees() {
        List<EmployeeEntity> employees = List.of(new EmployeeEntity(), new EmployeeEntity());
        when(employeeRepository.findAll()).thenReturn(employees);

        List<EmployeeEntity> result = service.getAllEmployees();

        assertEquals(2, result.size());
    }

    @Test
    public void testGetEmployeeById_Success() {
        EmployeeEntity employee = new EmployeeEntity();
        employee.setId(employeeId);
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

        EmployeeEntity result = service.getEmployeeById(employeeId);

        assertNotNull(result);
        assertEquals(employeeId, result.getId());
    }

    @Test
    public void testGetEmployeeById_NotFound() {
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.getEmployeeById(employeeId);
        });

        assertEquals("Invalid Employee Id", exception.getMessage());
    }

    @Test
    public void testDeleteEmployee_Success() {
        when(employeeRepository.deleteEmployeeEntitiesById(employeeId)).thenReturn(1);

        boolean deleted = service.deleteEmployee(employeeId);

        assertTrue(deleted);
    }

    @Test
    public void testDeleteEmployee_Failure() {
        when(employeeRepository.deleteEmployeeEntitiesById(employeeId)).thenReturn(0);

        boolean deleted = service.deleteEmployee(employeeId);

        assertFalse(deleted);
    }

    @Test
    public void testSaveEmployee() {
        EmployeeEntity employee = new EmployeeEntity();
        employee.setId(employeeId);

        when(employeeRepository.save(employee)).thenReturn(employee);

        UUID result = service.saveEmployee(employee);

        assertEquals(employeeId, result);
    }

    @Test
    public void testAddPermissionToEmployee_Success() {
        EmployeeEntity employee = spy(new EmployeeEntity());
        PermissionEntity permission = spy(new PermissionEntity());

        when(permissionRepository.findById(anyLong())).thenReturn(Optional.of(permission));
        when(employeeRepository.findById(any(UUID.class))).thenReturn(Optional.of(employee));

        boolean result = service.addPermissionToEmployee(employeeId, permissionId);

        verify(employee).addPermission(permission);
        verify(permission).addEmployee(employee);
        verify(employeeRepository).save(employee);
        verify(permissionRepository).save(permission);
        assertTrue(result);
    }

    @Test
    public void testAddPermissionToEmployee_PermissionNotFound() {
        when(permissionRepository.findById(permissionId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.addPermissionToEmployee(employeeId, permissionId);
        });

        assertEquals("Invalid Permission Id", exception.getMessage());
    }

    @Test
    public void testAddPermissionToEmployee_EmployeeNotFound() {
        PermissionEntity permission = new PermissionEntity();
        when(permissionRepository.findById(permissionId)).thenReturn(Optional.of(permission));
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.addPermissionToEmployee(employeeId, permissionId);
        });

        assertEquals("Invalid Employee Id", exception.getMessage());
    }

    @Test
    public void testRemovePermissionFromEmployee_Success() {
        EmployeeEntity employee = spy(new EmployeeEntity());
        PermissionEntity permission = spy(new PermissionEntity());

        when(permissionRepository.findById(permissionId)).thenReturn(Optional.of(permission));
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

        boolean result = service.removePermissionFromEmployee(employeeId, permissionId);

        verify(employee).removePermission(permission);
        verify(permission).removeEmployee(employee);
        verify(employeeRepository).save(employee);
        verify(permissionRepository).save(permission);
        assertTrue(result);
    }

    @Test
    public void testGetPermissions() {
        List<PermissionEntity> permissions = List.of(new PermissionEntity(), new PermissionEntity());
        when(permissionRepository.findAll()).thenReturn(permissions);

        List<PermissionEntity> result = service.getPermissions();

        assertEquals(2, result.size());
    }
}
