package org.example.loja.services;

import org.example.loja.config.security.JwtTokenProvider;
import org.example.loja.repository.AdminMasterRepository;
import org.example.loja.repository.StoreAdminRepository;
import org.example.loja.repository.StoreManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService{

    @Autowired
    private StoreAdminRepository storeAdminRepository;

    @Autowired
    private StoreManagerRepository storeManagerRepository;

    @Lazy
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AdminMasterRepository adminMasterRepository;

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(UserDetailsService.class);

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        log.debug("Attempting to load user with identifier: {}", identifier);

        String email = jwtTokenProvider.getUsernameFromToken(identifier);
        String userType = jwtTokenProvider.getRoleFromToken(identifier);
        return switch (userType) {
            case "ADMIN_MASTER" -> loadAdminMasterUser(email);
            case "STORE_ADMIN" -> loadStoreAdminUser(email);
            case "STORE_MANAGER" -> loadStoreManagerUser(email);
            default -> {
                log.error("Unknown user type: {}", userType);
                throw new UsernameNotFoundException("Unknown user type");
            }
        };
    }

    private UserDetails loadAdminMasterUser(String email) {
        return adminMasterRepository.findByEmail(email)
                .map(admin -> {
                    if (admin.getRole() == null || admin.getRole().isEmpty()) {
                        throw new UsernameNotFoundException("Admin Master has no roles assigned");
                    }
                    return new org.springframework.security.core.userdetails.User(
                            admin.getEmail(),
                            admin.getPassword(),
                            admin.getRole().stream()
                                    .map(role -> new SimpleGrantedAuthority(role.getName()))
                                    .collect(Collectors.toList())
                    );
                })
                .orElseThrow(() -> new UsernameNotFoundException("Invalid Admin Master Credentials"));
    }

    private UserDetails loadStoreAdminUser(String email) {
        return storeAdminRepository.findByEmail(email)
                .map(storeAdmin -> {
                    if (storeAdmin.getRole() == null || storeAdmin.getRole().isEmpty()) {
                        throw new UsernameNotFoundException("Store Admin has no roles assigned");
                    }
                    return new org.springframework.security.core.userdetails.User(
                            storeAdmin.getEmail(),
                            storeAdmin.getPassword(),
                            storeAdmin.getRole().stream()
                                    .map(role -> new SimpleGrantedAuthority(role.getName()))
                                    .collect(Collectors.toList())
                    );
                })
                .orElseThrow(() -> new UsernameNotFoundException("Invalid Store Admin Credentials"));
    }

    public UserDetails loadStoreManagerUser(String email){
        return storeManagerRepository.findByEmail(email)
                .map(storeManager -> {
                    if (storeManager.getRole() == null || storeManager.getRole().isEmpty()) {
                        throw new UsernameNotFoundException("Store Manager has no roles assigned");
                    }
                    return new org.springframework.security.core.userdetails.User(
                            storeManager.getEmail(),
                            storeManager.getPassword(),
                            storeManager.getRole().stream()
                                    .map(role -> new SimpleGrantedAuthority(role.getName()))
                                    .collect(Collectors.toList())
                    );
                }).orElseThrow(() -> new UsernameNotFoundException("Invalid Store Manager Credentials"));
    }
}
