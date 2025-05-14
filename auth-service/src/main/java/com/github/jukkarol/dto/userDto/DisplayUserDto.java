package com.github.jukkarol.dto.userDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DisplayUserDto {
    private Long id;

    private String name;

    private String email;

    private Integer  balance;

    private String accountNumber;

    private Date createdAt;

    private Date updatedAt;
}