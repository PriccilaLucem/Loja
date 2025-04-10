package org.example.loja.dto;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class ProductDTO {
    private Long id;

    private String name;

    private Double price;

    private String description;

    private String image;

    private String brand;

    private Integer quantity;

    private Set<Long> categories;

    private Long storeId;
}
