package org.example.loja.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "admin_logs")
public class AdminLogs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private Double lat;

    @Column(nullable = false)
    private Double lon;

    @ManyToOne
    @JoinColumn(name = "store_admin_id")
    private StoreAdmin storeAdmin;

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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLon() {
        return lon;
    }
    public void setLon(Double lon) {
        this.lon = lon;
    }
    public void setLat(Double lat) {
        this.lat = lat;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}

