package org.example.loja.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Entity
@Table(name = "admin_masters")
@Getter
@Setter
@NoArgsConstructor
public class AdminMasterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String email;
    private String password;

    @OneToMany(mappedBy = "adminMaster", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<RoleEntity> role = new HashSet<>();
}
