package tech.silva.connectcrm.dtos.client;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import tech.silva.connectcrm.models.Client;

public record ClientCreateDTO(
        @NotBlank
        String name,
        @NotBlank @Email(message = "email format is invalid", regexp = "^[a-z0-9.+-]+@[a-z0-9.-]+\\.[a-z]{2,}$")
        String email,
        @NotBlank
        String phone,
        @NotBlank
        String document,
        @NotBlank
        String address
){
    public static Client toClient(ClientCreateDTO clientDto){
        return new Client(
                clientDto.name,
                clientDto.email,
                clientDto.phone,
                clientDto.document,
                clientDto.address
        );
    }
}
