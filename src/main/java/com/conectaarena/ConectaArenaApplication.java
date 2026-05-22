package com.conectaarena;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication(excludeName = {
		"org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration",
		"org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration"
})
public class ConectaArenaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConectaArenaApplication.class, args);
	}
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}