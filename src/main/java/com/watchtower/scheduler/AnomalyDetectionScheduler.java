package com.watchtower.scheduler;

import com.watchtower.model.AnomalyReport;
import com.watchtower.model.ApiMetric;
import com.watchtower.repository.AnomalyReportRepository;
import com.watchtower.repository.ApiMetricRepository;
import com.watchtower.service.GroqService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class AnomalyDetectionScheduler {

    private final ApiMetricRepository apiMetricRepository;
    private final AnomalyReportRepository anomalyReportRepository;
    private final GroqService groqService;

    @Scheduled(fixedRate = 60000) // Run every minute
    public void detectAnomalies() {
        log.info("Starting anomaly detection scan...");

        // Analyze metrics from the last 5 minutes to keep it relevant
        LocalDateTime startTime = LocalDateTime.now().minusMinutes(5);
        List<ApiMetric> metrics = apiMetricRepository.findMetricsAfter(startTime);

        if (metrics.isEmpty()) {
            log.info("No metrics found for analysis.");
            return;
        }

        Map<String, List<ApiMetric>> metricsByEndpoint = metrics.stream()
                .collect(Collectors.groupingBy(ApiMetric::getEndpoint));

        metricsByEndpoint.forEach((endpoint, endpointMetrics) -> {
            if (endpointMetrics.size() < 2) {
                // Skip endpoints with insufficient data
                return;
            }

            double average = endpointMetrics.stream()
                    .mapToLong(ApiMetric::getResponseTimeMs)
                    .average()
                    .orElse(0.0);

            double variance = endpointMetrics.stream()
                    .mapToDouble(m -> Math.pow(m.getResponseTimeMs() - average, 2))
                    .average()
                    .orElse(0.0);

            double stdDev = Math.sqrt(variance);

            // Anomaly threshold: Avg + 2 * StdDev
            double threshold = average + (2 * stdDev);

            log.info("Endpoint: {}, Count: {}, Avg: {}, StdDev: {}, Threshold: {}",
                    endpoint, endpointMetrics.size(), String.format("%.2f", average), String.format("%.2f", stdDev),
                    String.format("%.2f", threshold));

            // Find anomalies in the current batch (that haven't been reported? For
            // simplicity, we just re-scan the window)

            for (ApiMetric m : endpointMetrics) {
                if (m.getResponseTimeMs() > threshold && m.getResponseTimeMs() > 100) { // Also ensure it's not just a
                                                                                        // tiny micro-fluctuation
                    log.warn("Anomaly detected for {}: {}ms (limit: {}ms)", endpoint, m.getResponseTimeMs(), threshold);

                    // Check if we already reported this recently to avoid spam (optional)
                    // For now, just generate report

                    String analysis = groqService.analyzeAnomaly(endpoint, m.getResponseTimeMs(), average, stdDev);

                    AnomalyReport report = AnomalyReport.builder()
                            .endpoint(endpoint)
                            .observedLatency((double) m.getResponseTimeMs())
                            .baselineLatency(average)
                            .standardDeviation(stdDev)
                            .rootCauseAnalysis(analysis)
                            .detectedAt(LocalDateTime.now())
                            .build();

                    anomalyReportRepository.save(report);
                }
            }
        });

        log.info("Anomaly detection scan completed.");
    }
}
