package com.iot.parking.security;

import com.iot.parking.user.User;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtil {


	private final String secretKey = "secret";
	private long accessTokenValidity = 60 * 60 * 1000;

	private final JwtParser jwtParser;

	private final String TOKEN_HEADER = "Authorization";
	private final String TOKEN_PREFIX = "Bearer ";

	public JwtUtil() {
		this.jwtParser = Jwts.parser().setSigningKey(secretKey);
	}

	public String createToken(User user) {
		Claims claims = Jwts.claims().setSubject(user.getUsername());
		claims.put("id", user.getId());
		claims.put("name", user.getName());
		claims.put("surname", user.getSurname());
		claims.put("role", user.getRole().name());
		Date tokenCreateTime = new Date();
		Date tokenValidity = new Date(tokenCreateTime.getTime() + TimeUnit.MINUTES.toMillis(accessTokenValidity));
		return Jwts.builder()
				.setClaims(claims)
				.setExpiration(tokenValidity)
				.signWith(SignatureAlgorithm.HS256, secretKey)
				.compact();
	}

	private Claims parseJwtClaims(String token) {
		return jwtParser.parseClaimsJws(token).getBody();
	}

	public Claims resolveClaims(HttpServletRequest req) {
		try {
			String token = resolveToken(req);
			if (token != null) {
				return parseJwtClaims(token);
			}
			return null;
		} catch (ExpiredJwtException ex) {
			req.setAttribute("expired", ex.getMessage());
			throw ex;
		} catch (Exception ex) {
			req.setAttribute("invalid", ex.getMessage());
			throw ex;
		}
	}

	public String resolveToken(HttpServletRequest request) {

		String bearerToken = request.getHeader(TOKEN_HEADER);
		if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
			return bearerToken.substring(TOKEN_PREFIX.length());
		}
		return null;
	}

	public boolean validateClaims(Claims claims) throws AuthenticationException {
		try {
			return claims.getExpiration().after(new Date());
		} catch (Exception e) {
			throw e;
		}
	}


}