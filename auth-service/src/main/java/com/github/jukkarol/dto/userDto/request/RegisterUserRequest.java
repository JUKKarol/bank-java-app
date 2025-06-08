package com.github.jukkarol.dto.userDto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUserRequest {
    @NotEmpty
    @Size(min=4, max=32)
    private String name;

    @NotEmpty
    @Email
    private String email;

    @NotEmpty
    @Size(min=8, max=32)
    private String password;
}
