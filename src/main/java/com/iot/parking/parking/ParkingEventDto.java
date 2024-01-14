package com.iot.parking.parking;

import lombok.Data;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link ParkingEvent}
 */
@Data
public class ParkingEventDto implements Serializable {
	Long id;
	String rfid;
	LocalDateTime timestamp;
	boolean entry;
}