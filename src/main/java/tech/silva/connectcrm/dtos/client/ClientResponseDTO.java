package tech.silva.connectcrm.dtos.client;

import tech.silva.connectcrm.dtos.user.UserResponseDTO;
import tech.silva.connectcrm.models.Client;
import java.util.List;
import java.util.stream.Collectors;

public record ClientResponseDTO(
        Long id,
        String name,
        String email,
        String phone,
        String document,
        String address,
        UserResponseDTO user
) {
    public static ClientResponseDTO toClientDto(Client client){
        return new ClientResponseDTO(
                client.getId(),
                client.getName(),
                client.getEmail(),
                client.getPhone(),
                client.getDocument(),
                client.getAddress(),
                UserResponseDTO.toUserDto(client.getUser())
        );
    }

    public static List<ClientResponseDTO> toList(List<Client> clients){
        return clients.stream().
                map(client -> toClientDto(client)).collect(Collectors.toList());
    }
}
