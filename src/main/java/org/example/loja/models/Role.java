package org.example.loja.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column( nullable = false)
    private String name;

    @ManyToMany(mappedBy = "roles")
    private List<Costumer> costumers = new ArrayList<>();


    @ManyToMany(mappedBy = "roles")
    private List<StoreAdmin> storeAdmin = new ArrayList<>();

    @Column
    private String description;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
