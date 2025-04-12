package tech.silva.connectcrm.dtos;
import tech.silva.connectcrm.enums.Role;

public record UserResponseDTO (
         Long id,
         String name,
         String email,
         Role role
){
}
