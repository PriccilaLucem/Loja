package org.example.loja.services;

import org.example.loja.entities.EmployeeEntity;
import org.example.loja.entities.PermissionEntity;
import org.example.loja.repository.EmployeeRepository;
import org.example.loja.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    public List<EmployeeEntity> getAllEmployees(){
        return employeeRepository.findAll();
    }

    public EmployeeEntity getEmployeeById(UUID uuid){
        return employeeRepository.findById(uuid).orElseThrow(()-> new IllegalArgumentException("Invalid Employee Id"));
    }

    public boolean deleteEmployee(UUID uuid){
        int affectedRows = employeeRepository.deleteEmployeeEntitiesById(uuid);
        return affectedRows > 0;
    }

    public UUID saveEmployee(EmployeeEntity employeeEntity){
        return employeeRepository.save(employeeEntity).getId();
    }

    public boolean addPermissionToEmployee(UUID employeeId, long permissionId){
        PermissionEntity permission = permissionRepository.findById(permissionId).orElseThrow(()-> new IllegalArgumentException("Invalid Permission Id"));
        EmployeeEntity employee = employeeRepository.findById(employeeId).orElseThrow(()-> new IllegalArgumentException("Invalid Employee Id"));
        employee.addPermission(permission);
        permission.addEmployee(employee);
        employeeRepository.save(employee);
        permissionRepository.save(permission);
        return true;
    }
    public boolean removePermissionFromEmployee(UUID employeeId, long permissionId) {
        PermissionEntity permission = permissionRepository.findById(permissionId).orElseThrow(() -> new IllegalArgumentException("Invalid Permission Id"));
        EmployeeEntity employee = employeeRepository.findById(employeeId).orElseThrow(() -> new IllegalArgumentException("Invalid Employee Id"));
        employee.removePermission(permission);
        permission.removeEmployee(employee);
        employeeRepository.save(employee);
        permissionRepository.save(permission);
        return true;
    }

    public List<PermissionEntity> getPermissions(){
        return permissionRepository.findAll();
    }
}
