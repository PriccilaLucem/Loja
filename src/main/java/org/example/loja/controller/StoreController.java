package org.example.loja.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.loja.config.security.JwtTokenProvider;
import org.example.loja.entities.StoreEntity;
import org.example.loja.services.StoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@Tag(name = "Store Controller", description = "Endpoints for Store management")
@RequestMapping("/api/v1/store")
public class StoreController {
    @Autowired
    private StoreService storeService;

    private static final Logger log = LoggerFactory.getLogger(StoreController.class);

    @PostMapping
    @Operation(summary = "Create a Store", description = "Creates a new store with the given details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Store created successfully", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected internal error", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = Map.class)))
    })
    public ResponseEntity<?> postStore(@RequestBody StoreEntity store,
                                       @RequestHeader("Authorization") String token) {
        try {
            UUID storeAdminId = JwtTokenProvider.extractIdFromToken(token);
            log.info("Creating store for admin ID: {}", storeAdminId);

            long id = storeService.saveStore(store, storeAdminId);

            log.info("Store created successfully. Store ID: {}, StoreAdmin ID: {}", id, storeAdminId);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Store created", "id", id, "storeAdminId", storeAdminId));
        } catch (IllegalArgumentException e) {
            log.warn("Invalid input for store creation: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error while creating store", e);
            return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred"));
        }
    }

    @PutMapping(value = "/{id}")
    @Operation(summary = "Update a Store", description = "Updates the store details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Store updated successfully", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "Store not found", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected internal error", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "403", description = "Not Authorized to deactivate store", content = @Content(schema = @Schema(implementation = Map.class)))
    })
    public ResponseEntity<?> putStore(@RequestBody StoreEntity store,
                                      @RequestHeader("Authorization") String token,
                                      @PathVariable long id) {
        UUID storeAdminId = JwtTokenProvider.extractIdFromToken(token);
        store.setId(id);
        log.info("Attempting to update store ID: {} by admin ID: {}", id, storeAdminId);

        try {
            if (storeService.verifyIfIsAuthorized(storeAdminId, store.getId())) {
                log.warn("Unauthorized update attempt on store ID: {} by admin ID: {}", id, storeAdminId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "You are not authorized to update this store"));
            }

            boolean affectedRows = storeService.updateStore(store);
            if (affectedRows) {
                log.info("Store updated successfully. Store ID: {}", id);
                return ResponseEntity.accepted().body(Map.of("message", "Store updated"));
            } else {
                log.warn("Store not found during update. Store ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Store not found"));
            }
        } catch (IllegalArgumentException e) {
            log.warn("Invalid input for store update: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error while updating store", e);
            return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred"));
        }
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Deactivate a Store", description = "Deactivates a store by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Store deactivated successfully", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "Store not found", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "403", description = "Not Authorized to deactivate store", content = @Content(schema = @Schema(implementation = Map.class)))
    })
    public ResponseEntity<?> deactivateStore(@PathVariable long id,
                                             @RequestHeader("Authorization") String token) {
        UUID storeAdminId = JwtTokenProvider.extractIdFromToken(token);
        log.info("Attempting to deactivate store ID: {} by admin ID: {}", id, storeAdminId);

        try {
            if (storeService.verifyIfIsAuthorized(storeAdminId, id)) {
                log.warn("Unauthorized deactivation attempt on store ID: {} by admin ID: {}", id, storeAdminId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "You are not authorized to deactivate this store"));
            }

            boolean isDeactivated = storeService.deleteStore(id);
            if (isDeactivated) {
                log.info("Store deactivated successfully. Store ID: {}", id);
                return ResponseEntity.accepted().body(Map.of("message", "Store deactivated"));
            } else {
                log.warn("Store not found during deactivation. Store ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Store not found"));
            }
        } catch (IllegalArgumentException e) {
            log.warn("Invalid input for store deactivation: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
