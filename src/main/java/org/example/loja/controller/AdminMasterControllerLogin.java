package org.example.loja.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.loja.config.security.JwtTokenProvider;
import org.example.loja.dto.LoginDTO;
import org.example.loja.entities.AdminMasterEntity;
import org.example.loja.services.AdminMasterService;
import org.example.loja.util.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Tag(name = "Admin Master Controller", description = "Endpoints for Admin Master management")
@RequestMapping("/admin/master/login")
public class AdminMasterControllerLogin {

    @Autowired
    private AdminMasterService adminMasterService;

    @Autowired
    private JwtTokenProvider provider;

    @Operation(
            summary = "Authenticate master admin",
            description = "Allows login for a master admin using email and password, returning a JWT token if authentication is successful."
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
                    responseCode = "500",
                    description = "Unexpected error occurred",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"An unexpected error occurred\" }"))
            )
    })
    @PostMapping
    public ResponseEntity<?> login(@RequestBody LoginDTO login) {
        try {
            AdminMasterEntity adminMaster = adminMasterService.getAdminMasterByEmail(login.getEmail());
            if (!Authorization.isAuthorized(login.getPassword(), adminMaster.getPassword())) {
                return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
            }
            String token = provider.generateAdminMasterToken(adminMaster);
            return ResponseEntity.ok().body(Map.of("token", token));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred"));
        }
    }
}