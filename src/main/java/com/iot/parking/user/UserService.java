package com.iot.parking.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final VehicleRepository vehicleRepository;
	private final UserMapper userMapper;

	public UserDto registerUser(UserRequest request) {
		User user = new User();
		user.setUsername(request.getUsername());
		user.setPassword(request.getPassword());
		userRepository.save(user);

		Vehicle vehicle = new Vehicle();
		vehicle.setUser(user);
		vehicle.setRfid(request.getRfid());
		vehicle.setLicensePlate(request.getLicensePlate());
		vehicleRepository.save(vehicle);
		return userMapper.toDto(user, vehicle);
	}
}
