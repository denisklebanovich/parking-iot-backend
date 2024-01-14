package com.iot.parking.vehicle;

import com.iot.parking.user.User;
import jakarta.persistence.*;

@Entity
public class Vehicle {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@OneToOne
	@JoinColumn(name = "user_id")
	User user;

	String licensePlate;
	String type;
}
