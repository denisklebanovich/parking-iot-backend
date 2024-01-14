package com.iot.parking;

import com.iot.parking.parking.Parking;
import com.iot.parking.parking.ParkingRepository;
import com.iot.parking.user.User;
import com.iot.parking.user.UserRepository;
import com.iot.parking.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminLoader implements ApplicationRunner {
	private final UserRepository userRepository;
	private final ParkingRepository parkingRepository;

	private final String ADMIN_USERNAME = "admin";
	private final String ADMIN_PASSWORD = "admin";
	private final String USER_USERNAME = "user";
	private final String USER_PASSWORD = "user";

	@Override
	public void run(ApplicationArguments args) throws Exception {
		User user = new User();
		user.setRfid("123456789");
		user.setName("Admin");
		user.setSurname("Admin");
		user.setRole(UserRole.ADMIN);
		user.setUsername(ADMIN_USERNAME);
		user.setPassword(ADMIN_PASSWORD);
		userRepository.save(user);

		user = new User();
		user.setName("User");
		user.setSurname("User");
		user.setRole(UserRole.USER);
		user.setUsername(USER_USERNAME);
		user.setPassword(USER_PASSWORD);
		userRepository.save(user);

		Parking parking = new Parking();
		parking.setAddress("ul. Wróblewskiego 27");
		parking.setCapacity(100);
		parking.setFreePlaces(100);
		parkingRepository.save(parking);
	}
}
