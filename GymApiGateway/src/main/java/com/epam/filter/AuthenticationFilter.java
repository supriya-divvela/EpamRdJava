package com.epam.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import com.epam.feign.AuthFeign;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

	@Autowired
	private RouteValidator validator;
//	@Autowired
//	private JwtUtil jwtUtil;

	@Autowired
	private AuthFeign authFeign;

	public AuthenticationFilter() {
		super(Config.class);
	}

	@Override
	public GatewayFilter apply(Config config) {
		return ((exchange, chain) -> {
			if (validator.isSecured.test(exchange.getRequest())) {
				if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
					throw new RuntimeException("missing authorization header");
				}

				String authHeader = exchange.getRequest().getHeaders()
						.get(org.springframework.http.HttpHeaders.AUTHORIZATION).get(0);
				if (authHeader != null && authHeader.startsWith("Bearer ")) {
					authHeader = authHeader.substring(7);
				}
				try {
					authFeign.validateToken(authHeader);

				} catch (Exception e) {
					System.out.println("invalid access...!");
					throw new RuntimeException("un authorized access to application");
				}
			}
			return chain.filter(exchange);
		});
	}

	public static class Config {

	}
}