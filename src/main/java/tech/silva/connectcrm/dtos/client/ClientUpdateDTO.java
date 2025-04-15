package tech.silva.connectcrm.dtos.client;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import tech.silva.connectcrm.models.Client;

public record ClientUpdateDTO(
        @NotNull
        Long id,
        @NotBlank
        String name,
        @NotBlank
        String phone,
        @NotBlank
        String document,
        @NotBlank
        String address
) {
    public static Client toClient(ClientUpdateDTO clientDto){
        return new Client(
                clientDto.id,
                clientDto.name,
                clientDto.phone,
                clientDto.document,
                clientDto.address
        );
    }
}
