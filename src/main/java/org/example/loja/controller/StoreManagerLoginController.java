package org.example.loja.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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

/**
 * Controller for handling store manager authentication and JWT token generation.
 */
@Tag(name = "Store Manager Authentication",
        description = "Endpoints for store manager login and JWT token generation")
@RestController
@RequestMapping("/api/v1/store-manager/login")
public class StoreManagerLoginController {

    @Autowired
    private JwtTokenProvider provider;

    @Autowired
    private StoreManagerService storeManagerService;

    @Operation(
            summary = "Authenticate store manager",
            description = """
            Authenticates a store manager with email and password credentials.
            Upon successful authentication, returns a JWT token that should be used 
            for subsequent authenticated requests.
            
            **Example Request:**
            ```json
            {
              "email": "manager@example.com",
              "password": "securePassword123"
            }
            ```
            """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Store manager login credentials",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginDTO.class),
                            examples = @ExampleObject(
                                    name = "Sample login",
                                    value = "{\"email\": \"manager@example.com\", \"password\": \"securePassword123\"}"
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Authentication successful",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(
                                    name = "Success response",
                                    value = "{\"token\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid credentials",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(
                                    name = "Error response",
                                    value = "{\"error\": \"Invalid credentials\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(
                                    name = "Error response",
                                    value = "{\"error\": \"An unexpected error occurred\"}"
                            )
                    )
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred"));
        }
    }
}