package tech.silva.connectcrm.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import tech.silva.connectcrm.models.AppUser;

public record UserCreateDTO(
        @NotBlank
        String name,
        @NotBlank @Email(message = "email format is invalid", regexp = "^[a-z0-9.+-]+@[a-z0-9.-]+\\.[a-z]{2,}$")
        String email,
        @NotBlank @Size(min = 6, max = 8)
        String password
) {
    public static AppUser ToUser(UserCreateDTO userDto){
        return new AppUser(
                userDto.name,
                userDto.email,
                userDto.password
        );
    }
}
