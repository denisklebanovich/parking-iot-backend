package com.iot.parking.parking;

import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link ParkingEvent}
 */
@Value
public class ParkingEventDto implements Serializable {
	Long id;
	Long vehicleId;
	String vehicleLicensePlate;
	LocalDateTime timestamp;
	boolean entry;
}