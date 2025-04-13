package org.example.loja.dto;

import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class ProductDTO {
    private Long id;

    private String name;

    private Double price;

    private String description;

    private List<String> images;

    private String brand;

    private Integer quantity;

    private Set<Long> categories;

    private Long storeId;
}
