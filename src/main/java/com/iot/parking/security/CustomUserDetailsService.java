package com.iot.parking.security;

import com.iot.parking.user.User;
import com.iot.parking.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {
	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User> userOptional = userRepository.findByUsername(username);
		return userOptional.map(user ->
				org.springframework.security.core.userdetails.User.builder()
						.username(user.getUsername())
						.password(user.getPassword())
						.roles(user.getRole().name())
						.build()
		).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
	}
}
