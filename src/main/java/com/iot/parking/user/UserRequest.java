package com.iot.parking.user;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
public class UserRequest {
	private String name;
	private String surname;
	private String username;
	private String password;
	private String rfid;
	private String licensePlate;
}
