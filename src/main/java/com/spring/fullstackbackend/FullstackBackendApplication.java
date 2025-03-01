package com.spring.fullstackbackend;

import com.spring.fullstackbackend.security.SecretKeyGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@SpringBootApplication
public class FullstackBackendApplication {

	private static final String SECRET_KEY_FILE = "secret-key.txt"; // File to store secret key

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
		Path path = Path.of(SECRET_KEY_FILE);

		if (Files.exists(path)) {
			// Read existing key
			return Files.readString(path).trim();
		} else {
			// Generate new key and save it
			String secretKey = SecretKeyGenerator.generateSecretKey();
			Files.writeString(path, secretKey);
			return secretKey;
		}
	}
}
