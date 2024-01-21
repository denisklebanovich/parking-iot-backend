package com.iot.parking.parking;

import com.iot.parking.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ParkingInfo {
	private Long id;
	private String address;
	private Integer capacity;
	private Integer freePlaces;
	private List<ParkedUser> users;

	@Data
	@AllArgsConstructor
	public static class ParkedUser {
		private String name;
		private String surname;
		private LocalDateTime entryTimestamp;
	}
}
