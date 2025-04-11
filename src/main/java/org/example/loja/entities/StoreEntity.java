package org.example.loja.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "stores")
@Getter
@Setter
@NoArgsConstructor
public class StoreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable(
            name = "store_location",
            joinColumns = @JoinColumn(name = "store_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "location_id", referencedColumnName = "id")
    )
    @NotNull
    private Set<AddressEntity> locations;

    private String imageURL;

    private String description;

    @NotNull
    private String phone;

    @NotNull
    private String email;

    private Boolean active;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductEntity> products;

    @ManyToOne
    @JoinColumn(name = "store_admin_id")
    private StoreAdminEntity storeAdmin;

    @OneToOne(mappedBy = "store")
    private StoreManagerEntity storeManager;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EmployeeEntity> employee = new HashSet<>();

    public void addProduct(ProductEntity product) {
        this.products.add(product);
    }
}
