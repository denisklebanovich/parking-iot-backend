package com.iot.parking.parking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParkingService {

	private final ParkingEventRepository parkingEventRepository;
	private final ParkingEventMapper parkingEventMapper;

	public List<ParkingEventDto> getParkingEvents() {
		return parkingEventRepository.findAll().stream()
				.map(parkingEventMapper::toDto)
				.toList();
	}
}
