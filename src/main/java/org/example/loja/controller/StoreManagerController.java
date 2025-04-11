package org.example.loja.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.loja.config.security.JwtTokenProvider;
import org.example.loja.dto.StoreManagerDTO;
import org.example.loja.services.StoreManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@Tag(name = "Store Manager Controller", description = "Endpoints for Store Manager management")
@RequestMapping("/api/v1/store/{storeId}/store-manager")
public class StoreManagerController {

    @Autowired
    private StoreManagerService storeManagerService;

    @Operation(summary = "Create a new store manager", description = "Creates a new manager for a specific store")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201", description = "Store manager created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "400", description = "Error in the request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "403", description = "User not authorized",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "500", description = "Unexpected error occurred",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))
            )
    })
    @PostMapping
    public ResponseEntity<?> post(
            @RequestBody StoreManagerDTO storeManager,
            @RequestHeader(name = "Authorization") String token
    ) {
        try {
            UUID adminId = JwtTokenProvider.extractIdFromToken(token);

            if (!storeManagerService.verifyIfIsAuthorized(adminId, storeManager.getStoreId()))
                return ResponseEntity.status(403).body(Map.of("error", "You are not authorized to update this store"));

            UUID id = storeManagerService.saveStoreManager(storeManager);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "Store manager created",
                    "id", id,
                    "storeId", storeManager.getStoreId()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred"));
        }
    }

    @Operation(summary = "Deactivate a store manager", description = "Deactivates the association between a manager and a store")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Store manager deactivated successfully"),
            @ApiResponse(responseCode = "403", description = "User not authorized"),
            @ApiResponse(responseCode = "404", description = "Store manager not found"),
            @ApiResponse(responseCode = "500", description = "Unexpected error occurred")
    })
    @PutMapping(value = "/deactivate/{id}")
    public ResponseEntity<?> putDeactivateManager(
            @PathVariable(value = "id") UUID storeManagerId
    ) {
        try {
           boolean isUpdated = storeManagerService.dissociateStoreManager(storeManagerId);
            if (isUpdated) {
                return ResponseEntity.accepted().body(Map.of("message", "Store manager deactivated"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Store manager not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred"));
        }
    }

    @Operation(summary = "Activate a store manager", description = "Reactivates the association between a manager and a store")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Store manager activated successfully"),
            @ApiResponse(responseCode = "403", description = "User not authorized"),
            @ApiResponse(responseCode = "404", description = "Store manager not found"),
            @ApiResponse(responseCode = "500", description = "Unexpected error occurred")
    })
    @PutMapping(value = "/activate/{id}")
    public ResponseEntity<?> putActiveManager(
            @PathVariable(value = "id") UUID storeManagerId,
            @PathVariable(value = "storeId") Long storeId
    ) {
        try {
            if (storeManagerService.activateStoreManager(storeManagerId, storeId)) {
                return ResponseEntity.accepted().body(Map.of("message", "Store manager activated"));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Store manager not found"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred"));
        }
    }

    @Operation(summary = "Update a store manager", description = "Updates the basic information of an existing store manager")
    @PutMapping
    public ResponseEntity<?> put(
            @RequestBody StoreManagerDTO storeManager
    ) {
        try {
            storeManagerService.saveStoreManager(storeManager);
            return ResponseEntity.accepted().body(Map.of("message", "Store manager updated"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred"));
        }
    }

    @Operation(summary = "Delete a store manager", description = "Removes a manager associated with a specific store from the system")
    @DeleteMapping(value = "/{storeManagerId}")
    public ResponseEntity<?> delete(
            @PathVariable UUID storeManagerId,
            @RequestHeader(name = "Authorization") String token
    ) {
        try {
            boolean isDeleted = storeManagerService.deleteStoreManager(storeManagerId);
            if (isDeleted) {
                return ResponseEntity.accepted().body(Map.of("message", "Store manager deleted"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Store manager not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred"));
        }
    }
}