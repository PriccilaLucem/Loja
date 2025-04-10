package org.example.loja.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.loja.inteface.LoggableUser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "store_managers")
@Getter
@Setter
@NoArgsConstructor
public class StoreManagerEntity implements LoggableUser {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    private String email;
    @NotNull
    private String password;

    private Boolean active;

    private Boolean status;

    @NotNull
    private String name;

    @NotNull
    private String cpf;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "store_manager_roles",
        joinColumns = @JoinColumn(name = "store_manager_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<RoleEntity> role = new HashSet<>();

    @OneToOne(cascade = CascadeType.PERSIST)
    private StoreEntity store;

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
