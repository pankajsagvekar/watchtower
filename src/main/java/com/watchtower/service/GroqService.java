package com.watchtower.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroqService {

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public String analyzeAnomaly(String endpoint, double observedLatency, double baselineLatency, double stdDev) {
        if (apiKey.equals("gsk_placeholder") || apiKey.isEmpty()) {
            return "Groq API Key not configured. Skipping AI analysis.";
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            String prompt = String.format(
                    "Analyze this API anomaly:\n" +
                            "Endpoint: %s\n" +
                            "Observed Latency: %.2f ms\n" +
                            "Normal Baseline: %.2f ms\n" +
                            "Standard Deviation: %.2f\n\n" +
                            "Provide a concise root cause analysis, potential impact, and mitigation steps.",
                    endpoint, observedLatency, baselineLatency, stdDev);

            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", prompt);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "llama-3.3-70b-versatile");
            requestBody.put("messages", new Object[] { message });
            requestBody.put("temperature", 0.7);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                return root.path("choices").get(0).path("message").path("content").asText();
            } else {
                return "Failed to get analysis from Groq: " + response.getStatusCode();
            }

        } catch (Exception e) {
            log.error("Error calling Groq API", e);
            return "Error during AI analysis: " + e.getMessage();
        }
    }
}
