package org.example.loja.services;

import org.example.loja.entities.AdminMasterEntity;
import org.example.loja.entities.RoleEntity;
import org.example.loja.repository.AdminMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminMasterService implements UserDetailsService {

    @Autowired
    private AdminMasterRepository adminMasterRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AdminMasterEntity admin = adminMasterRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid Credentials"));
        List<GrantedAuthority> authorities = admin.getRole().stream()
                .map((RoleEntity role) -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
        return new org.springframework.security.core.userdetails.User(
                admin.getEmail(),
                admin.getPassword(),
                authorities
        );
    }

    public AdminMasterEntity getAdminMasterByEmail(String email){
        return adminMasterRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Credentials"));
    }
}
