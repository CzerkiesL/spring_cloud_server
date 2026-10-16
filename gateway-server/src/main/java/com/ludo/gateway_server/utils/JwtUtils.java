package com.ludo.gateway_server.utils;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import com.ludo.gateway_server.config.JwtProperties;
import com.nimbusds.jose.util.StandardCharset;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {
	
	private final SecretKey accessKey;
	private final String issuer;
	private final String audience;
	
	public JwtUtils(JwtProperties properties) {
		this.accessKey = Keys.hmacShaKeyFor(properties.getAccessSecret().getBytes(StandardCharset.UTF_8));
		this.issuer = properties.getIssuer();
		this.audience = properties.getAudience();
	}
	
	public Claims validateAccessToken(String token) {
		return Jwts.parser()
				.verifyWith(accessKey)
				.requireIssuer(issuer)
				.requireAudience(audience)
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}
}
