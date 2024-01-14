package com.iot.parking.parking.event;

import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ParkingEventMapper {

	ParkingEventDto toDto(ParkingEvent parkingEvent);

	@InheritConfiguration(name = "toEntity")
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	ParkingEvent partialUpdate(ParkingEventDto parkingEventDto, @MappingTarget ParkingEvent parkingEvent);

	ParkingEvent fromRequest(ParkingEventRequest request);
}