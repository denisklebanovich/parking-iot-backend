package com.iot.parking.parking.event;

import com.iot.parking.exception.Reason;
import com.iot.parking.exception.ServiceException;
import com.iot.parking.parking.ParkingRepository;
import com.iot.parking.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ParkingEventService {
	private final ParkingEventRepository parkingEventRepository;
	private final ParkingEventMapper parkingEventMapper;
	private final ParkingRepository parkingRepository;
	private final UserRepository userRepository;

	public boolean registerParkingEvent(ParkingEventRequest request) {
		ParkingEvent parkingEvent = new ParkingEvent();
		var parking = parkingRepository.findById(request.getParkingId());
		if (parking.isEmpty()) {
			return false;
		}
		parkingEvent.setParking(parking.get());
		var user = userRepository.findByRfid(request.getRfid());
		if (user.isEmpty()) {
			return false;
		}
		parkingEvent.setUser(user.get());
		parkingEvent.setEntry(request.isEntry());
		parkingEvent.setTimestamp(LocalDateTime.now());
		return true;
	}

	public List<ParkingEventDto> getParkingEvents() {
		return parkingEventRepository.findAll().stream()
				.map(parkingEventMapper::toDto)
				.toList();
	}

	public List<ParkingEventDto> getUserParkingEvents(Long userId) {
		return parkingEventRepository.findAllByUserId(userId).stream()
				.map(parkingEventMapper::toDto)
				.toList();
	}

}
