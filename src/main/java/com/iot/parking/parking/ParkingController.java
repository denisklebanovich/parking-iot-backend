package com.iot.parking.parking;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
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

	@GetMapping("/events")
	public List<ParkingEventDto> getEvents() {
		//print user roles
		SecurityContextHolder.getContext().getAuthentication().getAuthorities().forEach(System.out::println);
		return parkingService.getParkingEvents();
	}
}
