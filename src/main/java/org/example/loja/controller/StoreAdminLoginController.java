package org.example.loja.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.loja.config.security.JwtTokenProvider;
import org.example.loja.dto.LoginDTO;
import org.example.loja.entities.StoreAdminEntity;
import org.example.loja.services.StoreAdminServices;
import org.example.loja.util.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Tag(name = "Store Admin Login Controller", description = "Endpoints for Store Admin management")
@RequestMapping("/api/v1/store-admin/login")
public class StoreAdminLoginController {

    @Autowired
    private StoreAdminServices storeAdminServices;
    @Autowired
    private JwtTokenProvider provider;

    @Operation(
            summary = "Authenticate administrator",
            description = "Allows an administrator to log in using email and password, returning a JWT token if successfully authenticated."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Login successful",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"token\": \"jwt_token_here\" }"))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Invalid credentials\" }"))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Request error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Error description\" }"))
            )
    })
    @PostMapping
    public ResponseEntity<?> login(@RequestBody LoginDTO login) {
        try {
            StoreAdminEntity storeAdmin = storeAdminServices.getStoreAdminByEmail(login.getEmail());

            if (Authorization.isAuthorized(login.getPassword(), storeAdmin.getPassword())) {
                String token = provider.generateUserAdminToken(storeAdmin);
                return ResponseEntity.ok().body(Map.of("token", token));
            }
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
