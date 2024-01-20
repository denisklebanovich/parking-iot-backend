package com.iot.parking.parking.statistics;

import lombok.Data;

import java.time.Duration;

@Data
public class ParkingStatistics {
	private Integer totalUsers;
	private Integer totalParkingPlaces;
	private Duration averageStayTime;
	private Long mostPopularParkingId;
	private String mostPopularParkingAddress;
}
