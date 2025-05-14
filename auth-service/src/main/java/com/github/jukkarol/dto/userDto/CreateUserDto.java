package com.github.jukkarol.dto.userDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserDto {
    @NotBlank
    @Size(min = 4, max = 20)
    private String name;

    @NotBlank
    @Size(max = 100)
    private String email;

    @NotBlank
    @Size(min = 8, max = 20)
    private String password;

    private Integer balance;

    @NotBlank
    @Size(min = 1, max = 20)
    @Pattern(regexp = "\\d+")
    private String accountNumber;
}