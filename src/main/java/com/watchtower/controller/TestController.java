package com.watchtower.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@RequestMapping("/test")
public class TestController {

    private final Random random = new Random();

    @GetMapping("/fast")
    public ResponseEntity<String> fastEndpoint(
            @org.springframework.web.bind.annotation.RequestParam(required = false, defaultValue = "0") int delay)
            throws InterruptedException {
        if (delay > 0) {
            Thread.sleep(delay);
        }
        return ResponseEntity.ok("Response with delay: " + delay + "ms");
    }

    @GetMapping("/slow")
    public ResponseEntity<String> slowEndpoint() throws InterruptedException {
        // Sleep between 500ms to 2000ms
        int delay = 500 + random.nextInt(1500);
        Thread.sleep(delay);
        return ResponseEntity.ok("Slow response after " + delay + "ms");
    }

    @GetMapping("/error")
    public ResponseEntity<String> errorEndpoint() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Intentional Error");
    }
}
