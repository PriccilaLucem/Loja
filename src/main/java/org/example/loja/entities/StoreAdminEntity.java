package org.example.loja.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "store_admins")
@Getter
@Setter
@NoArgsConstructor
public class StoreAdminEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private String email;

    private String password;

    private Boolean active;

    private Boolean status;

    @OneToMany(mappedBy = "storeAdmin", cascade = CascadeType.PERSIST, orphanRemoval = false)
    private List<AdminLogsEntity> adminLog;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "admin_roles",
            joinColumns = @JoinColumn(name = "admin_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<RoleEntity> role = new HashSet<>();

    @OneToMany(mappedBy = "storeAdmin", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StoreEntity> managedStore = new HashSet<>();
}
