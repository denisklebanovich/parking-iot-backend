package com.iot.parking.user;

import lombok.Data;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link User}
 */
@Data
public class UserDto {
	private String name;
	private String surname;
	private String licensePlate;
}