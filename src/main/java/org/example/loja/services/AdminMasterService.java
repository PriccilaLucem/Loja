package org.example.loja.services;

import org.example.loja.entities.AdminMasterEntity;
import org.example.loja.repository.AdminMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminMasterService {
    @Autowired
    private AdminMasterRepository adminMasterRepository;

    public AdminMasterEntity getAdminMasterByEmail(String email){
        return adminMasterRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Invalid Credentials"));
    }
}
