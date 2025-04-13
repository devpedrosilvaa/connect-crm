package tech.silva.connectcrm.dtos;
import tech.silva.connectcrm.enums.Role;
import tech.silva.connectcrm.models.AppUser;

import java.util.List;
import java.util.stream.Collectors;

public record UserResponseDTO (
         Long id,
         String name,
         String email,
         Role role
){
    public static UserResponseDTO toUserDto(AppUser user){
        return new UserResponseDTO(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole()
        );
    }
    public static List<UserResponseDTO> toList(List<AppUser> users) {
        return users.stream()
                .map(user -> toUserDto(user)).collect(Collectors.toList());
    }
}
