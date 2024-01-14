package com.iot.parking.parking;

import com.iot.parking.vehicle.Vehicle;
import jakarta.persistence.*;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
public class ParkingEvent {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@OneToOne
	@JoinColumn(name = "vehicle_id")
	Vehicle vehicle;

	private LocalDateTime timestamp;
	private boolean entry;
}
