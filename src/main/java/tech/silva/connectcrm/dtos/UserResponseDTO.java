package tech.silva.connectcrm.dtos;
import tech.silva.connectcrm.enums.Role;
import tech.silva.connectcrm.models.AppUser;

public record UserResponseDTO (
         Long id,
         String name,
         String email,
         Role role
){
    public static UserResponseDTO ToUserDto(AppUser user){
        return new UserResponseDTO(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole()
        );
    }
}
