package com.bluehawana.smrtmart;

import com.bluehawana.smrtmart.DataInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SmrtmartApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmrtmartApplication.class, args);
	}

	@Bean
	CommandLineRunner init(DataInitializer dataGenerator) {
		return args -> {
			try {
				dataGenerator.generateData();
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
	}
}