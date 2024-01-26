package com.iot.parking.parking.event;

import com.iot.parking.exception.Reason;
import com.iot.parking.exception.ServiceException;
import com.iot.parking.parking.ParkingRepository;
import com.iot.parking.user.ParkingHistory;
import com.iot.parking.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ParkingEventService {
	private final ParkingEventRepository parkingEventRepository;
	private final ParkingEventMapper parkingEventMapper;
	private final ParkingRepository parkingRepository;
	private final UserRepository userRepository;

	public void registerParkingEvent(ParkingEventRequest request) {
		ParkingEvent parkingEvent = new ParkingEvent();
		var parking = parkingRepository.findById(request.getParkingId())
				.orElseThrow(() -> new ServiceException(Reason.PARKING_NOT_FOUND));
		parkingEvent.setParking(parking);
		var user = userRepository.findByRfid(request.getRfid())
				.orElseThrow(() -> new ServiceException(Reason.USER_NOT_FOUND));
		var latestEntry = parkingEventRepository.findTopByUserIdOrderByTimestampDesc(user.getId());
		if (latestEntry.isPresent() && latestEntry.get().isEntry() == request.isEntry()) {
			throw new ServiceException(Reason.PARKING_CONFLICT);
		}
		parkingEvent.setUser(user);
		parkingEvent.setEntry(request.isEntry());
		parkingEvent.setTimestamp(LocalDateTime.now());
		parkingEventRepository.save(parkingEvent);
		if (parkingEvent.isEntry()) {
			parking.takePlace();
		} else {
			parking.leavePlace();
		}
	}

	public List<ParkingEventDto> getParkingEvents() {
		return parkingEventRepository.findAll().stream()
				.map(parkingEventMapper::toDto)
				.toList();
	}

	public List<ParkingHistory> getUserParkingEventHistory(Long userId) {
		List<ParkingEvent> userEvents = parkingEventRepository.findAllByUserIdOrderByTimestampDesc(userId);
		List<ParkingHistory> userParkingHistoryDTOs = new ArrayList<>();
		ParkingEvent exitEvent = null;
		for (ParkingEvent event : userEvents) {
			if (!event.isEntry()) {
				exitEvent = event;
			} else if (exitEvent != null) {
				userParkingHistoryDTOs.add(new ParkingHistory(
						exitEvent.getParking().getId(),
						exitEvent.getParking().getAddress(),
						exitEvent.getTimestamp(),
						event.getTimestamp()
				));
				exitEvent = null;
			}
		}
		return userParkingHistoryDTOs;
	}

}
