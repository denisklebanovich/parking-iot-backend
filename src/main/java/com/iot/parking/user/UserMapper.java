package com.iot.parking.user;

import com.iot.parking.vehicle.Vehicle;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
	User toEntity(UserDto userDto);

	@Mapping(target = "licensePlate", source = "vehicle.licensePlate")
	UserDto toDto(User user, Vehicle vehicle);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	User partialUpdate(UserDto userDto, @MappingTarget User user);
}