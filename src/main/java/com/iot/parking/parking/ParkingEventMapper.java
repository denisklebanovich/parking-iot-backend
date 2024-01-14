package com.iot.parking.parking;

import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ParkingEventMapper {
	@Mapping(source = "vehicleLicensePlate", target = "vehicle.licensePlate")
	@Mapping(source = "vehicleId", target = "vehicle.id")
	ParkingEvent toEntity(ParkingEventDto parkingEventDto);

	@InheritInverseConfiguration(name = "toEntity")
	ParkingEventDto toDto(ParkingEvent parkingEvent);

	@InheritConfiguration(name = "toEntity")
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	ParkingEvent partialUpdate(ParkingEventDto parkingEventDto, @MappingTarget ParkingEvent parkingEvent);

	@Mapping(source = "vehicleLicensePlate", target = "vehicle.licensePlate")
	@Mapping(source = "vehicleId", target = "vehicle.id")
	ParkingEvent fromRequest(ParkingEventRequest parkingEventRequest);
}