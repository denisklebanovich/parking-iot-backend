package com.iot.parking.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ParkingHistory {
	private Long parkingId;
	private String parkingAddress;
	private LocalDateTime entryTimestamp;
	private LocalDateTime exitTimestamp;
}
