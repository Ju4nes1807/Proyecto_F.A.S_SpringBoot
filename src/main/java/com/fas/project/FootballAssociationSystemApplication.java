package com.fas.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FootballAssociationSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(FootballAssociationSystemApplication.class, args);
	}

}
