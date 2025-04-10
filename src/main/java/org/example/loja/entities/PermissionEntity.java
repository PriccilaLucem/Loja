package org.example.loja.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "permissions")
@Getter
@Setter
@NoArgsConstructor
public class PermissionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String name;
    private String description;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "permission_employee", joinColumns = @JoinColumn(name = "permission_id"), inverseJoinColumns = @JoinColumn(name = "employee_id"))
    private Set<EmployeeEntity> employee;

    public void addEmployee(EmployeeEntity employee) {
        this.employee.add(employee);
    }
    public void removeEmployee(EmployeeEntity employee) {
        this.employee.remove(employee);
    }
}
