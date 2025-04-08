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
            String tokenValue = token.startsWith("Bearer ") ? token.substring(7) : token; // Remover o prefixo "Bearer " se existir
            UUID storeAdminId = JwtTokenProvider.extractIdFromToken(tokenValue);

            long id = storeService.saveStore(store, storeAdminId);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Store created", "id", id, "storeAdminId", storeAdminId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred"));
        }
    }

    @PutMapping
    @Operation(summary = "Update a Store", description = "Updates the store details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Store updated successfully", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "Store not found", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected internal error", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "403", description = "Not Authorized to deactivate store", content = @Content(schema = @Schema(implementation = Map.class)))
    })
    public ResponseEntity<?> putStore(@RequestBody StoreEntity store,
                                      @RequestHeader("Authorization") String token) {
        String tokenValue = token.startsWith("Bearer ") ? token.substring(7) : token;
        UUID storeAdminId = JwtTokenProvider.extractIdFromToken(tokenValue);

        try {
            if (storeService.verifyIfIsAuthorized(storeAdminId, store.getId())){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "You are not authorized to update this store"));
            };
            boolean affectedRows = storeService.updateStore(store);
            if (affectedRows) {
                return ResponseEntity.accepted().body(Map.of("message", "Store updated"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Store not found"));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
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
        String tokenValue = token.startsWith("Bearer ") ? token.substring(7) : token;
        UUID storeAdminId = JwtTokenProvider.extractIdFromToken(tokenValue);
        try {
            if(storeService.verifyIfIsAuthorized(storeAdminId, id)){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "You are not authorized to deactivate this store"));
            };

            boolean isDeactivated = storeService.deleteStore(id);
            if (isDeactivated) {
                return ResponseEntity.accepted().body(Map.of("message", "Store deactivated"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Store not found"));
            }
        }catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}