package com.iot.parking.parking.event;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingEventRepository extends JpaRepository<ParkingEvent, Long> {
}
