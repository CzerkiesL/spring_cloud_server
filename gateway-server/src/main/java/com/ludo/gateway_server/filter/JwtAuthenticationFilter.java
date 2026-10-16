package com.ludo.gateway_server.filter;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.ludo.gateway_server.exception.InvalidTokenException;
import com.ludo.gateway_server.utils.JwtUtils;

import io.jsonwebtoken.Claims;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements WebFilter {

	private JwtUtils jwtUtils;
	
	public JwtAuthenticationFilter(JwtUtils jwtUtils) {
		this.jwtUtils = jwtUtils;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();
		String path = request.getPath().value();
		
		if (isPublicPath(path)) { return chain.filter(exchange); }
		
		String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
		
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return Mono.error(
				new InvalidTokenException("Missing or invalid access token")
			);
		}
		
		String token = authHeader.substring(7);
		
		if (token.isEmpty()) {
			return Mono.error(
				new InvalidTokenException("Missing access token")
			);
		}
		
		try {
			Claims claims = jwtUtils.validateAccessToken(token);
			
			String userId = claims.getSubject();
			Object rolesClaim = claims.get("roles");
			Object permissionsClaim = claims.get("permissions");
			
			if (!(rolesClaim instanceof List<?> rawRoles)) {
				throw new IllegalArgumentException("Invalid roles claim");
			}
			if (!(permissionsClaim instanceof List<?> rawPermissions)) {
				throw new IllegalArgumentException("Invalid permissions claim");
			}
			
			List<String> roles = rawRoles.stream()
				.map(role -> {
					if (!(role instanceof String)) {
						throw new IllegalArgumentException("Invalid roles format");
					}
					return (String) role;
				}).toList();
			
			List<String> permissions = rawPermissions.stream()
				.map(permission -> {
					if (!(permission instanceof String)) {
						throw new IllegalArgumentException("Invalid permissions format");
					}
					return (String) permission;
				})
				.toList();
			
			ServerHttpRequest mutatedRequest = request.mutate()
				.headers(headers -> {
					headers.remove("X-User-Id");
					headers.remove("X-Roles");
					headers.remove("X-Permissions");
				})
				.header("X-User-Id", userId != null ? userId : "")
				.header("X-Roles", roles != null ? String.join(",", roles) : "")
				.header("X-Permissions", permissions != null ? String.join(",", permissions) : "")
				.build();
			
			return chain.filter(exchange.mutate().request(mutatedRequest).build());

		} catch (Exception e) {
			return Mono.error(
				new InvalidTokenException("Invalid or expired access token")
			);
		}
	}

	private boolean isPublicPath(String path) {
		return path.equals("/actuator/health")
			|| path.equals("/actuator/prometheus");
	}
}
