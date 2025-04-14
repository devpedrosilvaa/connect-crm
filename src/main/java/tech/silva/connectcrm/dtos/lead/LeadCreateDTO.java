package tech.silva.connectcrm.dtos.lead;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import tech.silva.connectcrm.models.AppUser;
import tech.silva.connectcrm.models.Lead;

public record LeadCreateDTO (
        @NotBlank
        String name,
        @NotBlank @Email(message = "email format is invalid", regexp = "^[a-z0-9.+-]+@[a-z0-9.-]+\\.[a-z]{2,}$")
        String email,
        @NotBlank
        String phone,
        @NotBlank
        String origin
){
    public static Lead toLead(LeadCreateDTO leadDTO){
        return new Lead(
                leadDTO.name,
                leadDTO.email,
                leadDTO.phone,
                leadDTO.origin
        );
    }
}
