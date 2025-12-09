package com.popjub.aiservice.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

	@Bean
	public WebClient reviewWebClient() {
		return WebClient.builder()
			// 로컬 개발용
			.baseUrl("http://localhost:18084") // URL Eureka 설정 시 변경 :http://review-service
			.build();
	}
}
