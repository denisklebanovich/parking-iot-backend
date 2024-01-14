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

	public ParkingEventDto registerParkingEvent(ParkingEventRequest request) {
		ParkingEvent parkingEvent = new ParkingEvent();
		var parking = parkingRepository.findById(request.getParkingId())
				.orElseThrow(() -> new ServiceException(Reason.PARKING_NOT_FOUND));
		parkingEvent.setParking(parking);
		var user = userRepository.findByRfid(request.getRfid())
				.orElseThrow(() -> new ServiceException(Reason.USER_NOT_FOUND));
		parkingEvent.setUser(user);
		parkingEvent.setEntry(request.isEntry());
		parkingEvent.setTimestamp(LocalDateTime.now());
		return parkingEventMapper.toDto(parkingEventRepository.save(parkingEvent));
	}

	public List<ParkingEventDto> getUserParkingEvents(Long userId) {
		return parkingEventRepository.findAllById(userId).stream()
				.map(parkingEventMapper::toDto)
				.toList();
	}

}
