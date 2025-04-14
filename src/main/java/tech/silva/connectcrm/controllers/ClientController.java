package tech.silva.connectcrm.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tech.silva.connectcrm.dtos.client.ClientCreateDTO;
import tech.silva.connectcrm.dtos.client.ClientResponseDTO;
import tech.silva.connectcrm.jwt.JwtUserDetails;
import tech.silva.connectcrm.models.Client;
import tech.silva.connectcrm.services.ClientService;

@RestController
@RequestMapping("/api/v1/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping
    public ResponseEntity<ClientResponseDTO> saveClient(@RequestBody @Valid ClientCreateDTO clientDTO,
                                                        @AuthenticationPrincipal JwtUserDetails userDetails){
        Client client = clientService.saveClient(ClientCreateDTO.toClient(clientDTO), userDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ClientResponseDTO.toClientDto(client));
    }
}
