package com.iot.parking.user;

import lombok.Data;

@Data
public class UserRequest {
	private String name;
	private String surname;
	private String username;
	private String password;
	private String rfid;
	private String licensePlate;

}
