package com.iot.parking.parking;

import com.iot.parking.user.UserDto;
import lombok.Data;

import java.util.List;

@Data
public class ParkingInfo {
	private Long id;
	private String address;
	private Integer capacity;
	private Integer freePlaces;
	private List<UserDto> users;
}
