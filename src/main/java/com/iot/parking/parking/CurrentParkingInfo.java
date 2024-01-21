package com.iot.parking.parking;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CurrentParkingInfo {
	private Long id;
	private String address;
	private Integer capacity;
	private Integer freePlaces;
	private LocalDateTime entryTimestamp;
}
