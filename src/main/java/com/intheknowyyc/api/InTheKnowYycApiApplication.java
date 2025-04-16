package com.intheknowyyc.api;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@SecurityScheme(name = "swagger-auth", scheme = "basic", type = SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER)
@SecurityScheme(name = "bearer-auth", scheme = "bearer", type = SecuritySchemeType.HTTP, bearerFormat= "JWT")
public class InTheKnowYycApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(InTheKnowYycApiApplication.class, args);
	}

}
