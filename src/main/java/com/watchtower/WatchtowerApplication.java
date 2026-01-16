package com.watchtower;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WatchtowerApplication {

	public static void main(String[] args) {
		SpringApplication.run(WatchtowerApplication.class, args);
	}

}
