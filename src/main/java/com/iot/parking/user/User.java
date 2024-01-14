package com.iot.parking.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@Column(unique = true, nullable = true)
	String rfid;

	@Enumerated(EnumType.STRING)
	UserRole role;

	String name;
	String surname;
	String username;
	String password;
	String licensePlate;
}
