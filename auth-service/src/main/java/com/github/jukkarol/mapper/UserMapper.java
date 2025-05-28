package com.github.jukkarol.mapper;

import com.github.jukkarol.dto.userDto.DisplayUserDto;
import com.github.jukkarol.model.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    DisplayUserDto userToDisplayUserDto(User user);

    List<DisplayUserDto> usersToDisplayUserDtos(List<User> users);
}