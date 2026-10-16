package com.ludo.config_server.config;

import org.springframework.amqp.rabbit.connection.ConnectionNameStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class RabbitMqConfig {

	@Bean
	ConnectionNameStrategy connectionNameStrategy(Environment environment) {
		return _ -> environment.getProperty("spring.application.name", "unknown-service");
	}
}
