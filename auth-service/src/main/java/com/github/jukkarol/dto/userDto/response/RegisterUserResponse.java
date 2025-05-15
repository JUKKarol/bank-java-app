package com.github.jukkarol.dto.userDto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUserResponse {
    private Long id;

    private String name;

    private String email;

    private Date createdAt;

    private Date updatedAt;
}
