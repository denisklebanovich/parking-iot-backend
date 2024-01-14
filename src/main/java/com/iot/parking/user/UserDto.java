package com.iot.parking.user;

import lombok.Data;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link User}
 */
@Data
public class UserDto {
	String username;
	String password;
}