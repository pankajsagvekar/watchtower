package com.watchtower.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity
@Table(name = "anomaly_reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnomalyReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String endpoint;

    @Column(nullable = false)
    private Double observedLatency;

    @Column(nullable = false)
    private Double baselineLatency;

    @Column(nullable = false)
    private Double standardDeviation;

    @Column(length = 5000)
    private String rootCauseAnalysis; // JSON or Text from LLM

    @Column(nullable = false)
    private LocalDateTime detectedAt;
}
