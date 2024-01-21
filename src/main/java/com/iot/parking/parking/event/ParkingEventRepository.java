package com.iot.parking.parking.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ParkingEventRepository extends JpaRepository<ParkingEvent, Long> {
	List<ParkingEvent> findAllById(Long id);

	List<ParkingEvent> findAllByUserId(Long userId);

	@Query("SELECT pe FROM ParkingEvent pe WHERE pe.parking.id = :parkingId AND pe.entry = true " +
			"AND NOT EXISTS (SELECT 1 FROM ParkingEvent e WHERE e.user.id = pe.user.id AND e.entry = false " +
			"AND e.timestamp > pe.timestamp)")
	List<ParkingEvent> findLatestEntriesByParkingId(@Param("parkingId") Long parkingId);

	@Query(value =
			"SELECT pe.parking_id " +
					"FROM parking_events pe " +
					"WHERE pe.entry = true " +
					"GROUP BY pe.parking_id " +
					"ORDER BY COUNT(pe.id) DESC " +
					"LIMIT 1",
			nativeQuery = true)
	Long findMostPopularParkingId();

	List<ParkingEvent> findAllByEntryTrueOrderByTimestamp();
	List<ParkingEvent> findAllByEntryFalseOrderByTimestamp();

	List<ParkingEvent> findAllByUserIdOrderByTimestampDesc(Long userId);

	Optional<ParkingEvent> findTopByUserIdOrderByTimestampDesc(Long userId);
}
