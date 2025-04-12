package org.example.loja.services;

import org.example.loja.config.security.JwtTokenProvider;
import org.example.loja.entities.AdminMasterEntity;
import org.example.loja.entities.RoleEntity;
import org.example.loja.repository.AdminMasterRepository;
import org.example.loja.repository.StoreAdminRepository;
import org.example.loja.repository.StoreManagerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class UserDetailsServiceTest {

    @Mock
    private AdminMasterRepository adminMasterRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private UserDetailsService userDetailsService;

    @BeforeEach
    void setup() {
        openMocks(this);
    }

    @Test
    void testLoadAdminMasterUser_Success() {
        String token = "valid_token";
        String email = "admin@example.com";

        when(jwtTokenProvider.getUsernameFromToken(token)).thenReturn(email);
        when(jwtTokenProvider.getRoleFromToken(token)).thenReturn("ADMIN_MASTER");

        RoleEntity role = new RoleEntity();
        role.setName("ROLE_ADMIN");

        AdminMasterEntity admin = new AdminMasterEntity();
        admin.setEmail(email);
        admin.setPassword("encryptedPassword");
        admin.setRole(Set.of(role));

        when(adminMasterRepository.findByEmail(email)).thenReturn(Optional.of(admin));

        UserDetails userDetails = userDetailsService.loadUserByUsername(token);

        assertEquals(email, userDetails.getUsername());
        assertEquals("encryptedPassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void testLoadAdminMasterUser_InvalidTokenRole() {
        String token = "invalid_token";

        when(jwtTokenProvider.getRoleFromToken(token)).thenReturn("UNKNOWN_ROLE");

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(token);
        });
    }

    @Test
    void testLoadAdminMasterUser_NoRoles() {
        String token = "token";
        String email = "admin@example.com";

        when(jwtTokenProvider.getUsernameFromToken(token)).thenReturn(email);
        when(jwtTokenProvider.getRoleFromToken(token)).thenReturn("ADMIN_MASTER");

        AdminMasterEntity admin = new AdminMasterEntity();
        admin.setEmail(email);
        admin.setPassword("pass");
        admin.setRole(Set.of()); // No roles

        when(adminMasterRepository.findByEmail(email)).thenReturn(Optional.of(admin));

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(token);
        });
    }

    @Test
    void testLoadAdminMasterUser_NotFound() {
        String token = "token";
        String email = "notfound@example.com";

        when(jwtTokenProvider.getUsernameFromToken(token)).thenReturn(email);
        when(jwtTokenProvider.getRoleFromToken(token)).thenReturn("ADMIN_MASTER");

        when(adminMasterRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(token);
        });
    }
}
