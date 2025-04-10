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

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AdminMasterService.class);

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Attempting to load user with email: {}", email);

        AdminMasterEntity admin = adminMasterRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("No user found for email: {}", email);
                    return new UsernameNotFoundException("Invalid Credentials");
                });

        if (admin.getRole() == null || admin.getRole().isEmpty()) {
            log.error("User with email {} has no roles assigned", email);
            throw new UsernameNotFoundException("User has no roles assigned");
        }

        List<GrantedAuthority> authorities = admin.getRole().stream()
                .map((RoleEntity role) -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());

        log.debug("Successfully loaded user with email: {}", email);

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
