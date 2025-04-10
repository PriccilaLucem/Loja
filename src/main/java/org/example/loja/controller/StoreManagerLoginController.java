package org.example.loja.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.loja.config.security.JwtTokenProvider;
import org.example.loja.dto.LoginDTO;
import org.example.loja.entities.StoreManagerEntity;
import org.example.loja.services.StoreManagerService;
import org.example.loja.util.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Tag(name = "Store Manager Login Controller", description = "Endpoints for Store Manager management")
@RequestMapping("/api/v1/store-manager/login")
public class StoreManagerLoginController {
    @Autowired
    private JwtTokenProvider provider;

    @Autowired
    private StoreManagerService storeManagerService;

    @Operation(
            summary = "Authenticate a store manager",
            description = "Authenticates a Store Manager with valid credentials (email and password) and returns a JWT token. If the credentials are invalid, an error message is returned."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Login successful. JWT token returned.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "User credentials are invalid.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal unexpected error.",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping
    public ResponseEntity<?> post(@RequestBody LoginDTO login) {
        try {
            StoreManagerEntity storeManager = storeManagerService.getStoreIdByStoreManagerEmail(login.getEmail());
            if (storeManager == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid credentials"));
            }
            if (!Authorization.isAuthorized(login.getPassword(), storeManager.getPassword())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid credentials"));
            }
            String token = provider.generateStoreManagerToken(storeManager);
            return ResponseEntity.ok().body(Map.of("token", token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred"));
        }
    }
}