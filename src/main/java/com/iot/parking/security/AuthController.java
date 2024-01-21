package com.iot.parking.security;

import com.iot.parking.user.UserDto;
import com.iot.parking.user.UserRepository;
import com.iot.parking.user.UserRequest;
import com.iot.parking.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
	private final AuthenticationManager authenticationManager;
	private final JwtUtil jwtUtil;
	private final UserRepository userRepository;
	private final UserService userService;


	@PostMapping(value = "/signin")
	public LoginResponse login(@RequestBody LoginRequest request) {

		try {
			Authentication authentication =
					authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
			String username = authentication.getName();
			var user = userRepository.findByUsername(username).orElseThrow(() -> new BadCredentialsException("User not found"));
			String token = jwtUtil.createToken(user);
			return new LoginResponse(user.getId(), token);

		} catch (BadCredentialsException e) {
			return null;
		}
	}

	@PostMapping("/register")
	public UserDto register(@RequestBody UserRequest request) {
		return userService.registerUser(request);
	}
}
