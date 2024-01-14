package com.iot.parking.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
	private final ObjectMapper mapper;

	public JwtAuthorizationFilter(JwtUtil jwtUtil, ObjectMapper mapper) {
		this.jwtUtil = jwtUtil;
		this.mapper = mapper;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		Map<String, Object> errorDetails = new HashMap<>();

		try {
			String accessToken = jwtUtil.resolveToken(request);
			if (accessToken == null) {
				filterChain.doFilter(request, response);
				return;
			}
			Claims claims = jwtUtil.resolveClaims(request);
			List<String> roles = (List<String>) claims.get("roles");
			List<GrantedAuthority> authorities = new ArrayList<>();
			for (String role : roles) {
				authorities.add(new SimpleGrantedAuthority(role));
			}
			if (claims != null & jwtUtil.validateClaims(claims)) {
				String username = claims.getSubject();
				Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}

		} catch (Exception e) {
			errorDetails.put("message", "Authentication Error");
			errorDetails.put("details", e.getMessage());
			response.setStatus(HttpStatus.FORBIDDEN.value());
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);

			mapper.writeValue(response.getWriter(), errorDetails);

		}
		filterChain.doFilter(request, response);
	}
}