package org.example.loja.models;

import jakarta.persistence.*;

@Entity
@Table(name = "costumer_logs")
public class CostumerLogs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "costumer_id")
    private Costumer costumer;

    private String action;

    @ManyToOne
    @JoinColumn(name = "costumer_log_id")
    private CostumerLogs costumerLogs;

    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
