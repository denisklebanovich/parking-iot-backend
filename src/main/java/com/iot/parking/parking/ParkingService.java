package com.iot.parking.parking;

import com.iot.parking.parking.event.ParkingEvent;
import com.iot.parking.parking.event.ParkingEventMapper;
import com.iot.parking.parking.event.ParkingEventRepository;
import com.iot.parking.parking.statistics.ParkingStatistics;
import com.iot.parking.user.UserDto;
import com.iot.parking.user.UserMapper;
import com.iot.parking.user.UserRepository;
import com.iot.parking.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ParkingService {

	private final ParkingEventRepository parkingEventRepository;
	private final ParkingRepository parkingRepository;
	private final UserRepository userRepository;
	private final ParkingEventMapper parkingEventMapper;
	private final UserMapper userMapper;

	public List<ParkingInfo> getParkingInfo() {
		return parkingRepository.findAll().stream()
				.map(this::toParkingInfo)
				.toList();
	}

	private ParkingInfo toParkingInfo(Parking parking) {
		ParkingInfo parkingInfo = new ParkingInfo();
		parkingInfo.setId(parking.getId());
		parkingInfo.setAddress(parking.getAddress());
		parkingInfo.setCapacity(parking.getCapacity());
		parkingInfo.setFreePlaces(parking.getFreePlaces());
		List<UserDto> users = parkingEventRepository.findLatestEntriesByParkingId(parking.getId()).stream()
				.map(ParkingEvent::getUser)
				.map(userMapper::toDto)
				.toList();
		parkingInfo.setUsers(users);
		return parkingInfo;
	}

	public ParkingStatistics getParkingStatistics() {
		ParkingStatistics parkingStatistics = new ParkingStatistics();
		parkingStatistics.setTotalUsers(userRepository.countAllByRole(UserRole.USER));
		parkingStatistics.setTotalParkingPlaces(parkingRepository.findAll().stream()
				.mapToInt(Parking::getCapacity)
				.sum());
		parkingStatistics.setAverageStayTime(getAverageStayTime());
		Long mostPopularParkingId = parkingEventRepository.findMostPopularParkingId();
		String mostPopularParkingAddress = parkingRepository
				.getReferenceById(mostPopularParkingId).getAddress();
		parkingStatistics.setMostPopularParkingId(mostPopularParkingId);
		parkingStatistics.setMostPopularParkingAddress(mostPopularParkingAddress);
		return parkingStatistics;
	}

	public Duration getAverageStayTime() {
		List<ParkingEvent> entryEvents = parkingEventRepository.findAllByEntryTrueOrderByTimestamp();
		List<Long> stayTimesInSeconds = entryEvents.stream()
				.map(entryEvent -> {
					ParkingEvent exitEvent = findExitEvent(entryEvents, entryEvent);
					return exitEvent != null ? Duration.between(entryEvent.getTimestamp(), exitEvent.getTimestamp()).getSeconds() : 0;
				})
				.toList();
		double averageStayTimeSeconds = stayTimesInSeconds.stream()
				.mapToLong(Long::longValue)
				.average()
				.orElse(0);
		return Duration.ofSeconds((long) averageStayTimeSeconds);
	}

	private ParkingEvent findExitEvent(List<ParkingEvent> entryEvents, ParkingEvent entryEvent) {
		return entryEvents.stream()
				.filter(exitEvent -> !exitEvent.isEntry() && exitEvent.getUser().equals(entryEvent.getUser()) && exitEvent.getTimestamp().isAfter(entryEvent.getTimestamp()))
				.min((e1, e2) -> e1.getTimestamp().compareTo(e2.getTimestamp()))
				.orElse(null);
	}

	public CurrentParkingInfo getCurrentParkingInfo(Long userId) {
		return parkingEventRepository.findByUserIdOrderByTimestamp(userId).stream()
				.filter(ParkingEvent::isEntry)
				.map(this::toCurrentParkingInfo)
				.findFirst()
				.orElse(null);
	}

	private CurrentParkingInfo toCurrentParkingInfo(ParkingEvent parkingEvent) {
		CurrentParkingInfo currentParkingInfo = new CurrentParkingInfo();
		currentParkingInfo.setId(parkingEvent.getParking().getId());
		currentParkingInfo.setAddress(parkingEvent.getParking().getAddress());
		currentParkingInfo.setCapacity(parkingEvent.getParking().getCapacity());
		currentParkingInfo.setFreePlaces(parkingEvent.getParking().getFreePlaces());
		currentParkingInfo.setEntryTimestamp(parkingEvent.getTimestamp());
		return currentParkingInfo;
	}
}
