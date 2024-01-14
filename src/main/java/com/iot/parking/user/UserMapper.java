package com.iot.parking.user;

import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
	User toEntity(UserDto userDto);

	UserDto toDto(User user);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	User partialUpdate(UserDto userDto, @MappingTarget User user);
}