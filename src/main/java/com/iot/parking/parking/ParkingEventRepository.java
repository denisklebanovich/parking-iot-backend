package com.iot.parking.parking;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingEventRepository extends JpaRepository<ParkingEvent, Long> {
}
