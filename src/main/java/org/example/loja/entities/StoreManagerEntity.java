package org.example.loja.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@Entity
@Table(name = "store_managers")
@Getter
@Setter
@NoArgsConstructor
public class StoreManagerEntity {

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

    @OneToOne(cascade = CascadeType.PERSIST)
    private StoreEntity store;
}
