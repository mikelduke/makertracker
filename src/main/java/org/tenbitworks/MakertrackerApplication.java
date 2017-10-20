package org.tenbitworks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MakertrackerApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(MakertrackerApplication.class, args);
	}
}
