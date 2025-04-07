package org.example.loja.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "costumers")
@Getter
@Setter
@NoArgsConstructor
public class CostumerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private String email;
    private String customerType;

    @OneToMany(mappedBy = "costumer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AddressEntity> address;

    @OneToMany(mappedBy = "costumer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CreditCardEntity> creditCard;

    @OneToMany(mappedBy = "costumer", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<CostumerLogsEntity> logs;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "costumer_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<RoleEntity> role;
}
