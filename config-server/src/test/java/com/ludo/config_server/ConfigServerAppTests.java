package com.ludo.config_server;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ConfigServerAppTests {

	@LocalServerPort
	private int port;
	
	private final RestTemplate restTemplate = new RestTemplate(); 
	
	@Test
	void contextLoads() {
		assertThat(true).isTrue();
	}
	
	@Test
	void checkHealth_whenStart_mustBeUp() {
		String url = "http://localhost:" + port + "/actuator/health";
		ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
		
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).contains("\"status\":\"UP\"");
	}
	
	@Test
	void callGitConfigEndpoint_whenStart_returnConfigForGatewayServer() {
		String url = "http://localhost:" + port + "/gateway-server/default";
		ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
		
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).contains("test.refresh-value");
	}
	
	@Test
	void callGitConfigEndpoint_whenStart_returnConfigForEurekaServer() {
		String url = "http://localhost:" + port + "/eureka-server/default";
		ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
		
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).contains("eureka");
	}
}
