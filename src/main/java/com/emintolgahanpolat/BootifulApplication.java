package com.emintolgahanpolat;

import com.emintolgahanpolat.api.configuration.captcha.CaptchaService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
public class BootifulApplication {

	public static void main(String[] args) {
		SpringApplication.run(BootifulApplication.class, args);
	}

	@Bean
	public CaptchaService getCaptchaGenerator() {
		return new CaptchaService();
	}
}
