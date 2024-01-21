package com.iot.parking.mock;

import com.github.javafaker.Faker;
import com.iot.parking.parking.Parking;
import com.iot.parking.parking.event.ParkingEvent;
import com.iot.parking.user.User;
import com.iot.parking.user.UserRole;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class MockDataGenerator {

	private final Faker faker;

	public MockDataGenerator() {
		Random random = new Random();
		random.setSeed(69);
		this.faker = new Faker(random);
	}

	public List<User> generateUsers(int count) {
		List<User> users = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			User user = new User();
			user.setRfid(faker.number().digits(10));
			user.setRole(UserRole.USER);
			user.setName(faker.name().firstName());
			user.setSurname(faker.name().lastName());
			user.setUsername(faker.name().username());
			user.setPassword(faker.internet().password());
			user.setLicensePlate(faker.bothify("??-####"));
			users.add(user);
		}
		return users;
	}

	public List<Parking> generateParkings(int count) {
		List<Parking> parkings = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			Parking parking = new Parking();
			parking.setAddress(faker.address().fullAddress());
			parking.setCapacity(faker.number().numberBetween(50, 200));
			parking.setFreePlaces(parking.getCapacity());
			parkings.add(parking);
		}
		return parkings;
	}

	public List<ParkingEvent> generateParkingEvents(List<User> users, List<Parking> parkings, int count) {
		List<ParkingEvent> parkingEvents = new ArrayList<>();
		LocalDateTime now = LocalDateTime.now();
		int pastCount = (int) (count * 0.9);
		int currentCount = count - pastCount;
		for (int i = 0; i < pastCount; i++) {
			ParkingEvent entryEvent = new ParkingEvent();
			entryEvent.setParking(getRandomElement(parkings));
			entryEvent.setUser(getRandomElement(users));
			var entryTimestamp = now.minusDays(ThreadLocalRandom.current().nextInt(60 * 24));
			entryEvent.setTimestamp(entryTimestamp); // Random timestamp within the last 24 hours
			entryEvent.setEntry(true);

			parkingEvents.add(entryEvent);

			// Generate a corresponding exit event
			ParkingEvent exitEvent = new ParkingEvent();
			exitEvent.setParking(entryEvent.getParking());
			exitEvent.setUser(entryEvent.getUser());
			exitEvent.setTimestamp(entryTimestamp.plusMinutes(ThreadLocalRandom.current().nextInt(1, 1200))); // Random exit timestamp within 1 to 60 minutes
			exitEvent.setEntry(false);

			parkingEvents.add(exitEvent);
		}
		for (int i = 0; i < currentCount; i++) {
			ParkingEvent entryEvent = new ParkingEvent();
			entryEvent.setParking(getRandomElement(parkings));
			entryEvent.setUser(getRandomElement(users));
			entryEvent.setTimestamp(now.minusMinutes(ThreadLocalRandom.current().nextInt(1, 60))); // Random timestamp within the last 60 minutes
			entryEvent.setEntry(true);
			parkingEvents.add(entryEvent);

			entryEvent.getParking().takePlace();
		}

		return parkingEvents;
	}

	private <T> T getRandomElement(List<T> list) {
		return list.get(ThreadLocalRandom.current().nextInt(list.size()));
	}
}
