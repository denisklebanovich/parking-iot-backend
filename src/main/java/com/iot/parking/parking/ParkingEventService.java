package com.iot.parking.parking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParkingEventService {
	private final ParkingEventRepository parkingEventRepository;
	private final ParkingEventMapper parkingEventMapper;

	public ParkingEventDto registerParkingEvent(ParkingEventRequest request) {
		ParkingEvent parkingEvent = parkingEventMapper.fromRequest(request);
		return parkingEventMapper.toDto(parkingEventRepository.save(parkingEvent));
	}


}
