package com.iot.parking.parking.event;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParkingEventRepository extends JpaRepository<ParkingEvent, Long> {
	List<ParkingEvent> findAllById(Long id);
}
