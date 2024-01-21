package com.iot.parking.parking;

import com.iot.parking.parking.event.ParkingEvent;
import com.iot.parking.parking.event.ParkingEventMapper;
import com.iot.parking.parking.event.ParkingEventRepository;
import com.iot.parking.parking.statistics.ParkingStatistics;
import com.iot.parking.user.UserMapper;
import com.iot.parking.user.UserRepository;
import com.iot.parking.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Comparator;
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
		List<ParkingInfo.ParkedUser> users = parkingEventRepository.findLatestEntriesByParkingId(parking.getId()).stream()
				.map(event -> new ParkingInfo.ParkedUser(event.getUser().getName(),
						event.getUser().getSurname(),
						event.getTimestamp()))
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

	public Integer getAverageStayTime() {
		List<ParkingEvent> entryEvents = parkingEventRepository.findAllByEntryTrueOrderByTimestamp();
		List<ParkingEvent> exitEvents = parkingEventRepository.findAllByEntryFalseOrderByTimestamp();
		double averageStayTimeSeconds = entryEvents.stream()
				.mapToDouble(entryEvent -> {
					ParkingEvent exitEvent = findExitEvent(exitEvents, entryEvent);
					return exitEvent != null ? Duration.between(entryEvent.getTimestamp(), exitEvent.getTimestamp()).getSeconds() : 0;
				})
				.average()
				.orElse(0);

		return (int) (averageStayTimeSeconds / 60);
	}

	private ParkingEvent findExitEvent(List<ParkingEvent> exitEvents, ParkingEvent entryEvent) {
		return exitEvents.stream()
				.filter(exitEvent ->
						exitEvent.getUser().equals(entryEvent.getUser()) &&
								(exitEvent.getTimestamp().isAfter(entryEvent.getTimestamp())))
				.min(Comparator.comparing(ParkingEvent::getTimestamp))
				.orElse(null);
	}

	public CurrentParkingInfo getCurrentParkingInfo(Long userId) {
		return parkingEventRepository.findTopByUserIdOrderByTimestampDesc(userId).stream()
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
