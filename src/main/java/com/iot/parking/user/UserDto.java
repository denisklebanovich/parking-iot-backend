package com.iot.parking.user;

import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link User}
 */
@Value
public class UserDto implements Serializable {
	String username;
	String password;
}