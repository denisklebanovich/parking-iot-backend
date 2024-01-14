package com.iot.parking.parking.event;

import com.iot.parking.parking.Parking;
import com.iot.parking.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class ParkingEvent {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@OneToOne
	@JoinColumn(name = "parking_id")
	Parking parking;

	@OneToOne
	@JoinColumn(name = "user_id")
	User user;

	private LocalDateTime timestamp;
	private boolean entry;
}
