package com.iot.parking.vehicle;

import com.iot.parking.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Vehicle {
	@Id
	String rfid;

	@OneToOne
	@JoinColumn(name = "user_id")
	User user;

	String licensePlate;
	String type;
}
