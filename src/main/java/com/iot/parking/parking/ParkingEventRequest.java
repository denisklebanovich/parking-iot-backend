package com.iot.parking.parking;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ParkingEventRequest {
	private String parkingId;
	private String rfid;
	private boolean entry;
	private LocalDateTime timestamp;
}
