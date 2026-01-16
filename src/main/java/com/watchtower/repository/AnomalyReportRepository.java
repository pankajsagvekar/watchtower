package com.watchtower.repository;

import com.watchtower.model.AnomalyReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnomalyReportRepository extends JpaRepository<AnomalyReport, Long> {
    List<AnomalyReport> findAllByOrderByDetectedAtDesc();
}
