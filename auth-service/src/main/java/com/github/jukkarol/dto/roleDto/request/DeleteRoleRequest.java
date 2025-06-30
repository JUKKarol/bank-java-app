package com.github.jukkarol.dto.roleDto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeleteRoleRequest {
    @NotEmpty
    @Email
    private String userEmail;
    
    @NotEmpty
    @Length(min=2, max=16)
    private String role;
}
