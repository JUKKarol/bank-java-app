package com.github.jukkarol.mapper;

import com.github.jukkarol.dto.userDto.DisplayUserDto;
import com.github.jukkarol.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    DisplayUserDto userToDisplayUserDto(User user);

    List<DisplayUserDto> usersToDisplayUserDtos(List<User> users);
}