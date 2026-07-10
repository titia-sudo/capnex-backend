package com.capnex_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CapnexBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(CapnexBackendApplication.class, args);
	}

}
