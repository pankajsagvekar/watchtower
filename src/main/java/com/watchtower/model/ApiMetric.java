package com.watchtower.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity
@Table(name = "api_metrics")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String endpoint;

    @Column(nullable = false)
    private String method;

    @Column(nullable = false)
    private Integer status;

    @Column(nullable = false)
    private Long responseTimeMs;

    @Column(nullable = false)
    private LocalDateTime timestamp;
}
