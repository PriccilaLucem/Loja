package org.example.loja.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
public class RoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column( nullable = false)
    private String name;

    @ManyToMany(mappedBy = "role")
    private List<CostumerEntity> costumer = new ArrayList<>();

    @ManyToOne()
    @JoinColumn(name = "admin_master_id")
    private AdminMasterEntity adminMaster;

    @ManyToMany(mappedBy = "role")
    private List<StoreAdminEntity> storeAdmin = new ArrayList<>();

    @Column
    private String description;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @ManyToMany(mappedBy = "role")
    private List<StoreManagerEntity> storeManager = new ArrayList<>();

    @OneToMany( fetch = FetchType.EAGER, cascade = CascadeType.ALL, targetEntity = EmployeeEntity.class)
    private Set<EmployeeEntity> employee = new HashSet<>();

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
