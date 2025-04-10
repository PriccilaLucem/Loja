package org.example.loja.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "employees")
public class EmployeeEntity {

    @Id
    @GeneratedValue(strategy =  GenerationType.UUID)
    private UUID id;

    private String name;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "cpf", unique = true, nullable = false)
    private String cpf;

    private Boolean active;
    private Boolean status;


    private String password;

    @ManyToOne
    @JoinColumn(name = "store")
    private StoreEntity store;

    @ManyToOne
    @JoinColumn(name = "role")
    private RoleEntity role;

    @ManyToOne()
    @JoinColumn(name = "employee_id")
    private AddressEntity address;

    @ManyToMany
    @JoinTable(
        name = "employee_roles",
        joinColumns = @JoinColumn(name = "employee_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<RoleEntity> roleSet = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "employee_permission",
            joinColumns = @JoinColumn(name = "permission_id"),
            inverseJoinColumns = @JoinColumn(name = "employee_id")
    )
    private Set<PermissionEntity> permissionSet = new HashSet<>();

    public void addPermission(PermissionEntity permission){
        this.permissionSet.add(permission);
    }

    public void removePermission(PermissionEntity permission){
        this.permissionSet.remove(permission);
    }
}
