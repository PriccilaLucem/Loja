package org.example.loja.services;

import org.example.loja.config.security.JwtTokenProvider;
import org.example.loja.entities.AdminMasterEntity;
import org.example.loja.repository.AdminMasterRepository;
import org.example.loja.repository.StoreAdminRepository;
import org.example.loja.repository.StoreManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminMasterService  {

    @Autowired
    private AdminMasterRepository adminMasterRepository;


    public AdminMasterEntity getAdminMasterByEmail(String email){
        return adminMasterRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Credentials"));
    }
}
