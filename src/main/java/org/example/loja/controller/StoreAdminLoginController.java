package org.example.loja.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.example.loja.config.security.JwtTokenProvider;
import org.example.loja.dto.loginDTO;
import org.example.loja.entities.StoreAdminEntity;
import org.example.loja.services.StoreAdminServices;
import org.example.loja.util.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/login")
public class StoreAdminLoginController {

    @Autowired
    private StoreAdminServices storeAdminServices;
    @Autowired
    private JwtTokenProvider provider;
    @Operation(
            summary = "Autenticar o administrador",
            description = "Permite que um administrador realize login com e-mail e senha, retornando um token JWT se autenticado com sucesso."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Login realizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"token\": \"jwt_token_aqui\" }"))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Credenciais inválidas",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Invalid credentials\" }"))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Erro no envio da requisição",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Descrição do erro\" }"))
            )
    })
    @PostMapping
    public ResponseEntity<?> login(@RequestBody loginDTO login) {
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