package com.famtwen.identity_service;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class IdentityServiceApplication {
	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure()
							  .filename(".env")
							  .directory(System.getProperty("user.dir")) // sẽ là thư mục root bạn chạy lệnh mvn spring-boot:run
							  .load();
		// Gán từng biến vào System properties để Spring Boot nhận
		dotenv.entries().forEach(entry ->
				System.setProperty(entry.getKey(), entry.getValue())
		);

		SpringApplication.run(IdentityServiceApplication.class, args);
	}
}

