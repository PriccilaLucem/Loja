package org.example.loja.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.loja.config.security.JwtTokenProvider;
import org.example.loja.dto.StoreManagerDTO;
import org.example.loja.services.StoreManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(StoreManagerController.class);

    @Operation(summary = "Create a new store manager", description = "Creates a new manager for a specific store")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Store manager created successfully"),
            @ApiResponse(responseCode = "400", description = "Error in the request"),
            @ApiResponse(responseCode = "403", description = "User not authorized"),
            @ApiResponse(responseCode = "500", description = "Unexpected error occurred")
    })
    @PostMapping
    public ResponseEntity<?> post(
            @RequestBody StoreManagerDTO storeManager,
            @RequestHeader(name = "Authorization") String token
    ) {
        logger.info("POST /store-manager - Creating store manager for storeId: {}", storeManager.getStoreId());
        try {
            UUID adminId = JwtTokenProvider.extractIdFromToken(token);
            logger.debug("Token validated. Admin ID: {}", adminId);

            UUID id = storeManagerService.saveStoreManager(storeManager);
            logger.info("Store manager created successfully. ID: {}", id);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "Store manager created",
                    "id", id,
                    "storeId", storeManager.getStoreId()
            ));
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid token provided. Error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error while creating store manager: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred"));
        }
    }

    @Operation(summary = "Deactivate a store manager", description = "Deactivates the association between a manager and a store")
    @PutMapping(value = "/deactivate/{id}")
    public ResponseEntity<?> putDeactivateManager(@PathVariable("id") UUID storeManagerId) {
        logger.info("PUT /store-manager/deactivate/{} - Request to deactivate store manager", storeManagerId);
        try {
            boolean isUpdated = storeManagerService.dissociateStoreManager(storeManagerId);
            if (isUpdated) {
                logger.info("Store manager deactivated successfully. ID: {}", storeManagerId);
                return ResponseEntity.accepted().body(Map.of("message", "Store manager deactivated"));
            } else {
                logger.warn("Store manager not found for deactivation. ID: {}", storeManagerId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Store manager not found"));
            }
        } catch (Exception e) {
            logger.error("Unexpected error while deactivating store manager: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred"));
        }
    }

    @Operation(summary = "Activate a store manager", description = "Reactivates the association between a manager and a store")
    @PutMapping(value = "/activate/{id}")
    public ResponseEntity<?> putActiveManager(
            @PathVariable("id") UUID storeManagerId,
            @PathVariable("storeId") Long storeId
    ) {
        logger.info("PUT /store-manager/activate/{} - Request to activate store manager for storeId: {}", storeManagerId, storeId);
        try {
            if (storeManagerService.activateStoreManager(storeManagerId, storeId)) {
                logger.info("Store manager activated successfully. ID: {}", storeManagerId);
                return ResponseEntity.accepted().body(Map.of("message", "Store manager activated"));
            }
            logger.warn("Store manager not found for activation. ID: {}", storeManagerId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Store manager not found"));
        } catch (Exception e) {
            logger.error("Unexpected error while activating store manager: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred"));
        }
    }

    @Operation(summary = "Update a store manager", description = "Updates the basic information of an existing store manager")
    @PutMapping("/{storeManagerId}")
    public ResponseEntity<?> put(@RequestBody StoreManagerDTO storeManager, @PathVariable UUID storeManagerId) {
        logger.info("PUT /store-manager - Updating store manager with ID: {}", storeManagerId);
        try {
            storeManagerService.saveStoreManager(storeManager);
            logger.info("Store manager updated successfully. ID: {}", storeManagerId);
            return ResponseEntity.accepted().body(Map.of("message", "Store manager updated"));
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid data for store manager update: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error while updating store manager: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred"));
        }
    }

    @Operation(summary = "Delete a store manager", description = "Removes a manager associated with a specific store from the system")
    @DeleteMapping(value = "/{storeManagerId}")
    public ResponseEntity<?> delete(
            @PathVariable UUID storeManagerId,
            @RequestHeader(name = "Authorization") String token
    ) {
        logger.info("DELETE /store-manager/{} - Attempting to delete store manager", storeManagerId);
        try {
            boolean isDeleted = storeManagerService.deleteStoreManager(storeManagerId);
            if (isDeleted) {
                logger.info("Store manager deleted successfully. ID: {}", storeManagerId);
                return ResponseEntity.accepted().body(Map.of("message", "Store manager deleted"));
            } else {
                logger.warn("Store manager not found for deletion. ID: {}", storeManagerId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Store manager not found"));
            }
        } catch (Exception e) {
            logger.error("Unexpected error while deleting store manager: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred"));
        }
    }
}
