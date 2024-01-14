package com.iot.parking.parking.event;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ParkingEventRequest {
	private Long parkingId;
	private String rfid;
	private boolean entry;
}
