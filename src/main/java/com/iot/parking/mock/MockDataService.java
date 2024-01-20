package com.iot.parking.mock;

import com.iot.parking.parking.Parking;
import com.iot.parking.parking.ParkingRepository;
import com.iot.parking.parking.event.ParkingEvent;
import com.iot.parking.parking.event.ParkingEventRepository;
import com.iot.parking.user.User;
import com.iot.parking.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MockDataService {

	@Autowired
	private MockDataGenerator mockDataGenerator;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ParkingRepository parkingRepository;

	@Autowired
	private ParkingEventRepository parkingEventRepository;

	public void generateMockData() {
		List<User> users = mockDataGenerator.generateUsers(100);
		List<Parking> parkings = mockDataGenerator.generateParkings(5);
		List<ParkingEvent> parkingEvents = mockDataGenerator.generateParkingEvents(users, parkings, 1000);

		userRepository.saveAll(users);
		parkingRepository.saveAll(parkings);
		parkingEventRepository.saveAll(parkingEvents);
	}
}
