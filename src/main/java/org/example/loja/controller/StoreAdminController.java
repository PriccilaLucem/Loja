package org.example.loja.controller;

import org.example.loja.entities.StoreAdminEntity;
import org.example.loja.services.StoreAdminServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/store-admins")
public class StoreAdminController {

    private static final Logger logger = LoggerFactory.getLogger(StoreAdminController.class);

    @Autowired
    private StoreAdminServices storeAdminServices;

    @GetMapping
    public ResponseEntity<List<StoreAdminEntity>> getAllStoreAdmins() {
        logger.info("GET /store-admins - Listing all store admins");
        List<StoreAdminEntity> storeAdmins = storeAdminServices.getAllStoreAdmins();

        if (storeAdmins.isEmpty()) {
            logger.info("No store admins found.");
            return ResponseEntity.ok().body(List.of());
        }

        logger.info("Found {} store admins", storeAdmins.size());
        return ResponseEntity.ok(storeAdmins);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getStoreAdminById(@PathVariable UUID id) {
        logger.info("GET /store-admins/{} - Getting store admin by ID", id);
        try {
            StoreAdminEntity storeAdminEntity = storeAdminServices.getStoreAdminById(id);
            logger.info("Store admin found: {}", storeAdminEntity.getEmail());
            return ResponseEntity.ok(storeAdminEntity);
        } catch (IllegalArgumentException e) {
            logger.warn("Store admin with ID {} not found", id);
            return ResponseEntity.status(404).body(Map.of("error", "Store Admin not found."));
        }
    }

    @PostMapping
    public ResponseEntity<?> saveStoreAdmin(@RequestBody StoreAdminEntity storeAdminEntity,
                                            @RequestParam(required = true) double lat,
                                            @RequestParam(required = true) double lon) {
        logger.info("POST /store-admins - Creating new store admin: {} at lat={}, lon={}",
                storeAdminEntity.getEmail(), lat, lon);

        try {
            UUID createdId = storeAdminServices.saveStoreAdmin(storeAdminEntity, lat, lon);
            logger.info("Store admin created successfully with ID {}", createdId);
            return ResponseEntity.status(201).body(Map.of("message", "Store Admin created.", "id", createdId));
        } catch (IllegalArgumentException e) {
            logger.error("Failed to create store admin: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStoreAdmin(@PathVariable UUID id) {
        logger.info("DELETE /store-admins/{} - Deleting store admin", id);
        try {
            if (!storeAdminServices.deleteStoreAdmin(id)) {
                logger.warn("Attempt to delete already deactivated admin with ID {}", id);
                return ResponseEntity.badRequest().body(Map.of("error", "Admin Already Deactivated"));
            }
            logger.info("Store admin with ID {} deleted successfully", id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to delete admin: store admin with ID {} not found", id);
            return ResponseEntity.status(404).body(Map.of("error", "Store Admin not found."));
        }
    }
}
