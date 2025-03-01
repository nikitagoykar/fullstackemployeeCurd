package com.spring.fullstackbackend;

import com.spring.fullstackbackend.security.SecretKeyGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;

@SpringBootApplication
public class FullstackBackendApplication {

	private static final String ENV_SECRET_KEY = "JWT_SECRET_KEY"; // Environment variable for secret key

	public static void main(String[] args) {
		SpringApplication.run(FullstackBackendApplication.class, args);
		System.out.println("Employee Service Running...");

		try {
			String secretKey = getOrGenerateSecretKey();
			System.out.println("Using Secret Key: " + secretKey);
		} catch (IOException e) {
			throw new RuntimeException("Failed to generate secret key", e);
		}
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	private static String getOrGenerateSecretKey() throws IOException {
		// Check if the secret key is available in the environment variables
		String secretKey = System.getenv(ENV_SECRET_KEY);

		if (secretKey != null && !secretKey.isEmpty()) {
			return secretKey; // Return the secret key from environment variables
		} else {
			// If not set in environment, generate and save to a file
			secretKey = SecretKeyGenerator.generateSecretKey();
			System.out.println("Generated Secret Key: " + secretKey);
			return secretKey;
		}
	}
}
