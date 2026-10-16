package com.ludo.gateway_server.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
	private String accessSecret;
	private String issuer;
	private String audience;
	
	public String getAccessSecret() { return accessSecret; }
	public void setAccessSecret(String accessSecret) { this.accessSecret = accessSecret; }
	
	public String getIssuer() { return issuer; }
	public void setIssuer(String issuer) { this.issuer = issuer; }
	
	public String getAudience() { return audience; }
	public void setAudience(String audience) { this.audience = audience; }
}
