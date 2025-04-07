package org.example.loja.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@RequestMapping("/api/v1/store-admins")
public class StoreAdminController {

    private static final Logger logger = LoggerFactory.getLogger(StoreAdminController.class);

    @Autowired
    private StoreAdminServices storeAdminServices;

    @Operation(
            summary = "Listar todos os administradores da loja",
            description = "Retorna uma lista de todos os administradores da loja registrados no sistema."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista de administradores retornada com sucesso",
            content = @Content(mediaType = "application/json")
    )
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

    @Operation(
            summary = "Obter administrador da loja por ID",
            description = "Busca e retorna os dados de um administrador da loja com base no ID fornecido."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Administrador encontrado com sucesso",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Administrador não encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Store Admin not found.\" }"))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getStoreAdminById(
            @Parameter(description = "ID do administrador da loja", example = "76b8cc94-bc5f-43d1-a25f-91bb11bdd59f")
            @PathVariable UUID id) {
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

    @Operation(
            summary = "Criar novo administrador da loja",
            description = "Cria um novo administrador da loja e retorna o ID gerado.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados do administrador da loja incluindo e-mail, senha e nome",
                    required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = StoreAdminEntity.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Administrador criado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"message\": \"Store Admin created.\", \"id\": \"76b8cc94-bc5f-43d1-a25f-91bb11bdd59f\" }"))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Erro ao criar administrador",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Descrição do erro\" }"))
            )
    })
    @PostMapping
    public ResponseEntity<?> saveStoreAdmin(
            @RequestBody StoreAdminEntity storeAdminEntity,
            @Parameter(description = "Latitude de localização associada ao administrador", example = "37.7749")
            @RequestParam(required = true) double lat,
            @Parameter(description = "Longitude de localização associada ao administrador", example = "-122.4194")
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

    @Operation(
            summary = "Deletar administrador da loja",
            description = "Deleta logicamente um administrador da loja.",
            parameters = @Parameter(name = "id", required = true, description = "ID do administrador a ser deletado", example = "76b8cc94-bc5f-43d1-a25f-91bb11bdd59f")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Administrador deletado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Falha ao deletar (já desativado)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Admin Already Deactivated\" }"))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Administrador não encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Store Admin not found.\" }"))
            )
    })
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