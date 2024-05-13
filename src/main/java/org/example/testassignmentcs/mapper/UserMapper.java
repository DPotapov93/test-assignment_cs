package org.example.testassignmentcs.mapper;

import org.example.testassignmentcs.config.MapperConfig;
import org.example.testassignmentcs.dto.UserCreateRequestDto;
import org.example.testassignmentcs.dto.UserDto;
import org.example.testassignmentcs.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserDto toDto(User user);

    User toModel(UserCreateRequestDto requestDto);

    User toModelFromUserDto(UserDto userDto);
}
