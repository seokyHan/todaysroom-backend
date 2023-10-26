package com.todaysroom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing
@EnableScheduling
@SpringBootApplication
public class TodaysroomApplication {

	public static void main(String[] args) {
		SpringApplication.run(TodaysroomApplication.class, args);
	}

}
