package com.iot.parking.parking;

import com.iot.parking.parking.event.ParkingEventDto;
import com.iot.parking.parking.event.ParkingEventService;
import com.iot.parking.parking.statistics.ParkingStatistics;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/parking")
@RequiredArgsConstructor
public class ParkingController {
	private final ParkingService parkingService;
	private final ParkingEventService parkingEventService;

	@GetMapping("/events")
	public List<ParkingEventDto> getEvents() {
		//print user roles
		SecurityContextHolder.getContext().getAuthentication().getAuthorities().forEach(System.out::println);
		return parkingEventService.getParkingEvents();
	}

	@GetMapping(value = "/events", params = "userId")
	public List<ParkingEventDto> getUserEvents(Long userId) {
		return parkingEventService.getUserParkingEvents(userId);
	}

	@GetMapping("/info")
	public List<ParkingInfo> getParkingInfo() {
		return parkingService.getParkingInfo();
	}

	@GetMapping("/statistics")
	public ParkingStatistics getParkingStatistics() {
		return parkingService.getParkingStatistics();
	}
}
