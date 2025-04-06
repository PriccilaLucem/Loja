package org.example.loja.models;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
@Entity
@Table(name = "store_admins")
public class StoreAdmin {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private String email;

    private String password;

    @OneToMany(mappedBy = "storeAdmin", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Store> managedStores = new HashSet<>();

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Store> getManagedStores() {
        return managedStores;
    }

    public void setManagedStores(Set<Store> managedStores) {
        this.managedStores = managedStores;
    }
}
