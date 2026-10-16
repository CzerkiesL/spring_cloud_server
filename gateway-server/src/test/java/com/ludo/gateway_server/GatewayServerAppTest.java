package com.ludo.gateway_server;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GatewayServerAppTest {

	@LocalServerPort
	private int port;

    private WebTestClient webTestClient;
	
    @BeforeEach
    void setUp() {
        this.webTestClient = WebTestClient.bindToServer()
            .baseUrl("http://localhost:" + port)
            .build();
    }

	@Test
	void contextLoads() {
		assertThat(true).isTrue();
	}

	@Test
	void checkHealth_whenStart_mustBeUp() {
		 webTestClient.get()
	         .uri("http://localhost:" + port + "/actuator/health")
	         .exchange()
	         .expectStatus()
	         .isEqualTo(HttpStatus.OK)
	         .expectBody()
	         .jsonPath("$.status")
	         .isEqualTo("UP");
	}

	@Test
	void rootEndpointShouldNotCrash() {
		var res = webTestClient.get()
		        .uri("http://localhost:" + port + "/")
		        .exchange()
		        .returnResult(String.class);
		
		int status = res.getStatus().value();
		
		assertThat(status).isIn(HttpStatus.OK.value(), HttpStatus.NOT_FOUND.value(), HttpStatus.SERVICE_UNAVAILABLE.value(), HttpStatus.UNAUTHORIZED.value());
	}
}
