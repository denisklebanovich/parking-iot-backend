package com.iot.parking.parking;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Parking {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String address;
	private Integer capacity;
	private Integer freePlaces;

	public void takePlace() {
		freePlaces--;
	}

	public void leavePlace() {
		freePlaces++;
	}
}
