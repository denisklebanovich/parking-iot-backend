package com.iot.parking.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final UserMapper userMapper;

	public UserDto registerUser(UserRequest request) {
		User user = new User();
		user.setUsername(request.getUsername());
		user.setPassword(request.getPassword());
		user.setRole(UserRole.USER);

		userRepository.save(user);
		return userMapper.toDto(user);
	}
}
