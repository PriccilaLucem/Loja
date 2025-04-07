package org.example.loja.controller;

import org.example.loja.config.security.JwtTokenProvider;
import org.example.loja.dto.loginDTO;
import org.example.loja.entities.AdminMasterEntity;
import org.example.loja.services.AdminMasterService;
import org.example.loja.util.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/admin/master/login")
public class AdminMasterControllerLogin {
    @Autowired
    private AdminMasterService adminMasterService;

    @Autowired
    private JwtTokenProvider provider;

    @PostMapping
    public ResponseEntity<?> login(@RequestBody loginDTO login){
        try {
            AdminMasterEntity adminMaster = adminMasterService.getAdminMasterByEmail(login.getEmail());
            if(!Authorization.isAuthorized(login.getPassword(), adminMaster.getPassword())){
                return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
            }
            String token = provider.generateAdminMasterToken(adminMaster);
            return ResponseEntity.ok().body(Map.of("token",token));
        }catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred"));
        }
    }
}
