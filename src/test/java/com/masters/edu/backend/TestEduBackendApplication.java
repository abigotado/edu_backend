package com.masters.edu.backend;

import org.springframework.boot.SpringApplication;

public class TestEduBackendApplication {

	public static void main(String[] args) {
		SpringApplication.from(EduBackendApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
