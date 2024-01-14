package com.iot.parking.parking;

import lombok.Data;

@Data
public class ParkingEventRequest {
	private String parkingId;
	private String licensePlate;
	private boolean entry;
}
