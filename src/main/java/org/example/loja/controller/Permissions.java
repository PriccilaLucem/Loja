package org.example.loja.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.example.loja.entities.PermissionEntity;
import org.example.loja.services.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/permissions")
public class Permissions {

    @Autowired
    private EmployeeService employeeService;

    private static final Logger log = LoggerFactory.getLogger(Permissions.class);

    @Operation(summary = "Get all permissions", description = "Retrieve all available permissions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved permissions",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PermissionEntity.class, type = "array"))),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred")
    })
    @GetMapping("/list/permission")
    public ResponseEntity<?> getPermission() {
        try {
            List<PermissionEntity> permissions = employeeService.getPermissions();
            log.info("Retrieved permissions: {}", permissions);
            return ResponseEntity.ok(permissions);
        } catch (Exception e) {
            log.error("Error retrieving permissions", e);
            return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred"));
        }
    }

}
