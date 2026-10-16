package com.ludo.gateway_server.exception;

import java.time.LocalDateTime;

import org.springframework.web.server.WebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

@Component
@Order(-2)
public class GlobalErrorWebExceptionHandler implements WebExceptionHandler  {

	private record ErrorConfig(HttpStatus status, String errorCode) {}
	private final ObjectMapper objectMapper;
	
	public GlobalErrorWebExceptionHandler(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}
	
	@Override
	public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
		ServerHttpResponse response = exchange.getResponse();
		
		if (response.isCommitted()) {
			return Mono.error(ex);
		}
		
		ErrorConfig errorConfig = switch (ex) {
		case InvalidTokenException _ ->
		new ErrorConfig(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN");
		
		default -> null;
		};
		
		if (errorConfig == null) {
			return Mono.error(ex);
		}
		
		response.setStatusCode(errorConfig.status());
		response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
		
		ErrorDetails errorDetails = new ErrorDetails(
			LocalDateTime.now(),
			ex.getMessage(),
			exchange.getRequest().getPath().value(),
			errorConfig.errorCode()
		);
		
		try {
			byte[] bytes = objectMapper.writeValueAsBytes(errorDetails);
			
			return response.writeWith(
					Mono.just(response.bufferFactory().wrap(bytes))
					);
			
		} catch (Exception e) {
			
			return response.setComplete();
			
		}
	}
}
