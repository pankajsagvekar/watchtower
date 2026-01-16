package com.watchtower.repository;

import com.watchtower.model.ApiMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ApiMetricRepository extends JpaRepository<ApiMetric, Long> {

    @Query("SELECT m FROM ApiMetric m WHERE m.timestamp >= :startTime")
    List<ApiMetric> findMetricsAfter(@Param("startTime") LocalDateTime startTime);

    // Find distinct endpoints to iterate over
    @Query("SELECT DISTINCT m.endpoint FROM ApiMetric m")
    List<String> findDistinctEndpoints();

    List<ApiMetric> findByEndpointAndTimestampAfter(String endpoint, LocalDateTime timestamp);
}
