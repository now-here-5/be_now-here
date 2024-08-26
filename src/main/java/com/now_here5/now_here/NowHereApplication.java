package com.now_here5.now_here;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class NowHereApplication {

	public static void main(String[] args) {
		SpringApplication.run(NowHereApplication.class, args);
	}

}
