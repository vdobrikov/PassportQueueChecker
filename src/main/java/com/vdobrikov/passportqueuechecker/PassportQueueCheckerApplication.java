package com.vdobrikov.passportqueuechecker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PassportQueueCheckerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PassportQueueCheckerApplication.class, args);
	}
}
