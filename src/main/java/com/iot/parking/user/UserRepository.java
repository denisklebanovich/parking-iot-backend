package com.iot.parking.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>{
	Optional<User> findByUsername(String username);
	Optional<User> findByRfid(String rfid);
	Integer countAllByRole(UserRole role);
	boolean existsByUsername(String username);
	boolean existsByRfid(String rfid);
}
