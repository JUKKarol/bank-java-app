package com.github.jukkarol.dto.roleDto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetRolesResponse {
    private Long userId;

    private List<String> roles;
}
