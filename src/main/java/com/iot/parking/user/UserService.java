package com.iot.parking.user;

import com.iot.parking.exception.Reason;
import com.iot.parking.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final UserMapper userMapper;

	public UserDto registerUser(UserRequest request) {
		if (userRepository.existsByUsername(request.getUsername()) || userRepository.existsByRfid(request.getRfid())) {
			throw new ServiceException(Reason.USER_ALREADY_EXISTS);
		}
		User user = new User();
		user.setUsername(request.getUsername());
		user.setPassword(request.getPassword());
		user.setName(request.getName());
		user.setSurname(request.getSurname());
		user.setRfid(request.getRfid());
		user.setLicensePlate(request.getLicensePlate());
		user.setRole(UserRole.USER);

		userRepository.save(user);
		return userMapper.toDto(user);
	}
}
