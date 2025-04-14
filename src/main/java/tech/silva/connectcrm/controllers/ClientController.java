package tech.silva.connectcrm.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tech.silva.connectcrm.dtos.client.ClientCreateDTO;
import tech.silva.connectcrm.dtos.client.ClientResponseDTO;
import tech.silva.connectcrm.dtos.lead.LeadResponseDTO;
import tech.silva.connectcrm.jwt.JwtUserDetails;
import tech.silva.connectcrm.models.Client;
import tech.silva.connectcrm.models.Lead;
import tech.silva.connectcrm.services.ClientService;

import java.util.List;

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

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') OR hasRole('MANAGER')")
    public ResponseEntity<List<ClientResponseDTO>> getAllClient(){
        List<Client> clients = clientService.getAllClient();
        if(clients.isEmpty())
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok().body(ClientResponseDTO.toList(clients));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<List<ClientResponseDTO>> getMyClients(@AuthenticationPrincipal JwtUserDetails userDetails) {
        List<Client> clients = clientService.getMyClients(userDetails.getId());
        if (!clients.isEmpty())
            return ResponseEntity.ok().body(ClientResponseDTO.toList(clients));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> getClientById(@PathVariable Long id,
                                                        @AuthenticationPrincipal JwtUserDetails userDetails) {
        Client client = clientService.getClientById(id, userDetails.getId());
        return ResponseEntity.ok().body(ClientResponseDTO.toClientDto(client));
    }
}
