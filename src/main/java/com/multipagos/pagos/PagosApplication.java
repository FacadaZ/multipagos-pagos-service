package com.multipagos.pagos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class PagosApplication implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(PagosApplication.class, args);
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/qrs/**")
				.addResourceLocations("file:" + System.getProperty("java.io.tmpdir") + "/qrs/");
		registry.addResourceHandler("/receipts/**")
				.addResourceLocations("file:" + System.getProperty("java.io.tmpdir") + "/receipts/");
	}

}
