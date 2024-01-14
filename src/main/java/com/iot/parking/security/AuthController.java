package com.iot.parking.security;

import com.iot.parking.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
	private final AuthenticationManager authenticationManager;
	private final JwtUtil jwtUtil;
	private final UserRepository userRepository;


	@PostMapping(value = "/login")
	public LoginResponse login(@RequestBody LoginRequest request) {

		try {
			Authentication authentication =
					authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
			String username = authentication.getName();
			var user = userRepository.findByUsername(username).orElseThrow(() -> new BadCredentialsException("User not found"));
			String token = jwtUtil.createToken(user);
			LoginResponse response = new LoginResponse(username, token);
			return response;

		} catch (BadCredentialsException e) {
			return null;
		}
	}
}
