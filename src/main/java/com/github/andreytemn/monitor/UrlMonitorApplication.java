package com.github.andreytemn.monitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class UrlMonitorApplication {

	public static void main(String[] args) {
		SpringApplication.run(UrlMonitorApplication.class, args);
	}

}
