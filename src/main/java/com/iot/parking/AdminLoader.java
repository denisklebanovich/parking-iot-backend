package com.iot.parking;

import com.iot.parking.mock.MockDataService;
import com.iot.parking.parking.Parking;
import com.iot.parking.parking.ParkingRepository;
import com.iot.parking.user.User;
import com.iot.parking.user.UserRepository;
import com.iot.parking.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminLoader implements ApplicationRunner {
	private final UserRepository userRepository;
	private final ParkingRepository parkingRepository;
	private final MockDataService mockDataService;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		User user = new User();
		user.setRfid("123456789");
		user.setName("Admin");
		user.setSurname("Admin");
		user.setRole(UserRole.ADMIN);
		String ADMIN_USERNAME = "admin";
		user.setUsername(ADMIN_USERNAME);
		String ADMIN_PASSWORD = "admin";
		user.setPassword(ADMIN_PASSWORD);
		userRepository.save(user);

		user = new User();
		user.setName("User");
		user.setSurname("User");
		user.setRole(UserRole.USER);
		String USER_USERNAME = "user";
		user.setUsername(USER_USERNAME);
		String USER_PASSWORD = "user";
		user.setPassword(USER_PASSWORD);
		userRepository.save(user);

		mockDataService.generateMockData();
	}
}
