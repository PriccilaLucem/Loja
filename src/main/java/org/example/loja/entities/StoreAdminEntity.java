package org.example.loja.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.loja.inteface.LoggableUser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "store_admins")
@Getter
@Setter
@NoArgsConstructor
public class StoreAdminEntity implements LoggableUser {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private String email;

    private String password;

    private Boolean active;

    private Boolean status;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "admin_roles",
            joinColumns = @JoinColumn(name = "admin_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<RoleEntity> role = new HashSet<>();

    @OneToMany(mappedBy = "storeAdmin", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StoreEntity> managedStore = new HashSet<>();

    @Override
    public UUID getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getEmail() {
        return this.email;
    }

    @Override
    public List<RoleEntity> getRoles(){
        return this.getRole().stream().toList();
    }
}
