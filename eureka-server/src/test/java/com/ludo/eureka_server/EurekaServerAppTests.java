package com.ludo.eureka_server;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EurekaServerAppTests {

	@LocalServerPort
	private int port;
	
	private final RestTemplate restTemplate = new RestTemplate();
	
	@Test
	void contextLoads() {
		assertThat(true).isTrue();
	}
	
	@Test
	void CheckHealth_whenStart_mustBeUp() {
		String url = "http://localhost:" + port + "/actuator/health/liveness";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("\"status\":\"UP\"");
	}
	
	@Test
	void eurekaHomepageMustBeOk() {
		String url = "http://localhost:" + port + "/";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Eureka");
	}
	
	@Test
	void callEurekaAppEndpoint_shouldReturnJson() {
		String url = "http://localhost:" + port + "/eureka/apps";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("\"applications\"");
	}
	
	@Test
	void eurekaShouldentRegisterItself() {
		String url = "http://localhost:" + port + "/eureka/apps";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).doesNotContain("EUREKA-SERVER");
	}
}
