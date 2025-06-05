package com.miempresa.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;
import java.sql.Connection;

@SpringBootApplication
public class ApiApplication implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(ApiApplication.class);
	private final DataSource dataSource;

	// Constructor que inyecta DataSource
	public ApiApplication(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
		logger.info("✅ Servidor API iniciado correctamente en http://localhost:8080");
	}

	@Override
	public void run(String... args) {
		try (Connection conn = dataSource.getConnection()) {
			if (conn.isValid(1)) {
				logger.info("✅ Conexión a la base de datos exitosa");
			} else {
				logger.error("❌ No se pudo validar la conexión a la base de datos");
			}
		} catch (Exception e) {
			logger.error("❌ Error al conectar con la base de datos: {}", e.getMessage());
		}
	}
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry){
				registry.addMapping("/**")
						.allowedOrigins("http://localhost:3000")
						.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
						.allowedHeaders("*")
						.allowCredentials(true);

			}
		};
	}
}
