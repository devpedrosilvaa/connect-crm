package tech.silva.connectcrm.dtos.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import tech.silva.connectcrm.models.AppUser;

public record UserUpdateDto(
        @NotNull
        Long id,
        @NotBlank
        String name,
        @NotBlank @Size(min = 6, max = 8)
        String password
) {
    public static AppUser toUser(UserUpdateDto userDto){
        return new AppUser(
                userDto.id,
                userDto.name,
                userDto.password
        );
    }
}
