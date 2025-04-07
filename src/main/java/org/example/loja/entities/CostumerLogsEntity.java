package org.example.loja.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "costumer_logs")
@Getter
@Setter
@NoArgsConstructor
public class CostumerLogsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "costumer_id", nullable = false)
    private CostumerEntity costumer;

    @Column(nullable = false)
    private String action;

    @Column(nullable = true, length = 500)
    private String description;
}