package org.example.loja.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.loja.entities.EmployeeEntity;
import org.example.loja.services.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@Tag(name = "Employee Controller", description = "Manage employees and their permissions")
@RestController
@RequestMapping("/api/v1/store/{storeId}/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    @Operation(summary = "Get all employees", description = "Retrieve all employees")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EmployeeEntity.class, type = "array")))
    })
    @GetMapping("/list")
    public ResponseEntity<?> getAllEmployees() {
        logger.info("Fetching all employees");
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @Operation(summary = "Create employee", description = "Create a new employee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "400", description = "Employee not created due to bad request")
    })
    @PostMapping
    public ResponseEntity<?> postEmployee(@RequestBody EmployeeEntity employee) {
        logger.info("Attempting to create a new employee: {}", employee.getName());
        UUID id = employeeService.saveEmployee(employee);
        if (id != null) {
            logger.info("Employee created with ID: {}", id);
            return ResponseEntity.ok().body(Map.of("message", "EmployeeCreated", "id", id));
        } else {
            logger.warn("Failed to create employee: {}", employee);
            return ResponseEntity.badRequest().body(Map.of("error", "Employee not created"));
        }
    }

    @Operation(summary = "Delete employee", description = "Delete an employee by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Employee successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Employee not found"),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred")
    })
    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable UUID id) {
        logger.info("Request to delete employee with ID: {}", id);
        try {
            boolean isDeleted = employeeService.deleteEmployee(id);
            if (isDeleted) {
                logger.info("Employee deleted with ID: {}", id);
                return ResponseEntity.noContent().build();
            }
            logger.warn("Employee not found with ID: {}", id);
            return ResponseEntity.status(404).body(Map.of("error", "Employee not found"));
        } catch (Exception e) {
            logger.error("Error deleting employee with ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred"));
        }
    }

    @Operation(summary = "Add permission to employee", description = "Add a permission to an employee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Permission added to employee"),
            @ApiResponse(responseCode = "404", description = "Employee not found"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred")
    })
    @PutMapping("/add/permission/{employeeId}/{permissionId}")
    public ResponseEntity<?> putEmployeePermission(@PathVariable UUID employeeId, @PathVariable Long permissionId) {
        logger.info("Adding permission {} to employee {}", permissionId, employeeId);
        try {
            boolean updated = employeeService.addPermissionToEmployee(employeeId, permissionId);
            if (updated) {
                logger.info("Permission {} added to employee {}", permissionId, employeeId);
                return ResponseEntity.ok().body(Map.of("message", "Permission added to employee"));
            } else {
                logger.warn("Employee not found with ID: {}", employeeId);
                return ResponseEntity.status(404).body(Map.of("error", "Employee not found"));
            }
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error adding permission {} to employee {}: {}", permissionId, employeeId, e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred"));
        }
    }

    @Operation(summary = "Remove permission from employee", description = "Remove a permission from an employee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Permission removed from employee"),
            @ApiResponse(responseCode = "404", description = "Employee not found"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred")
    })
    @PutMapping("/remove/permission/{employeeId}/{permissionId}")
    public ResponseEntity<?> removeEmployeePermission(@PathVariable UUID employeeId, @PathVariable Long permissionId) {
        logger.info("Removing permission {} from employee {}", permissionId, employeeId);
        try {
            boolean updated = employeeService.removePermissionFromEmployee(employeeId, permissionId);
            if (updated) {
                logger.info("Permission {} removed from employee {}", permissionId, employeeId);
                return ResponseEntity.ok().body(Map.of("message", "Permission removed from employee"));
            } else {
                logger.warn("Employee not found with ID: {}", employeeId);
                return ResponseEntity.status(404).body(Map.of("error", "Employee not found"));
            }
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error removing permission {} from employee {}: {}", permissionId, employeeId, e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred"));
        }
    }
}
