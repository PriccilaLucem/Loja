package org.example.loja.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ImageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    private String url;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    @OneToOne
    private StoreEntity store;

}

