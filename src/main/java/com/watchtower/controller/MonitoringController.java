package com.watchtower.controller;

import com.watchtower.model.AnomalyReport;
import com.watchtower.repository.AnomalyReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/monitoring")
@RequiredArgsConstructor
public class MonitoringController {

    private final AnomalyReportRepository anomalyReportRepository;

    @GetMapping("/anomalies")
    public ResponseEntity<List<AnomalyReport>> getAllAnomalies() {
        return ResponseEntity.ok(anomalyReportRepository.findAllByOrderByDetectedAtDesc());
    }

    @GetMapping("/latest")
    public ResponseEntity<AnomalyReport> getLatestAnomaly() {
        List<AnomalyReport> anomalies = anomalyReportRepository.findAllByOrderByDetectedAtDesc();
        if (anomalies.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(anomalies.get(0));
    }
}
