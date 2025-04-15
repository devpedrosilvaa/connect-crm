package tech.silva.connectcrm.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tech.silva.connectcrm.dtos.client.ClientCreateDTO;
import tech.silva.connectcrm.dtos.client.ClientResponseDTO;
import tech.silva.connectcrm.dtos.client.ClientUpdateDTO;
import tech.silva.connectcrm.dtos.lead.LeadResponseDTO;
import tech.silva.connectcrm.exceptions.ErrorMessage;
import tech.silva.connectcrm.jwt.JwtUserDetails;
import tech.silva.connectcrm.models.Client;
import tech.silva.connectcrm.models.Lead;
import tech.silva.connectcrm.services.ClientService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clients")
@Tag(name = "Clients", description = "Operations related to client management")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @Operation(
            summary = "Register a new client",
            description = "Creates a new client associated with the authenticated user",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Client successfully created",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClientResponseDTO.class))),
                    @ApiResponse(responseCode = "422", description = "Invalid input fields or client already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Access denied",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PostMapping
    public ResponseEntity<ClientResponseDTO> saveClient(@RequestBody @Valid ClientCreateDTO clientDTO,
                                                        @AuthenticationPrincipal JwtUserDetails userDetails) {
        Client client = clientService.saveClient(ClientCreateDTO.toClient(clientDTO), userDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ClientResponseDTO.toClientDto(client));
    }

    @Operation(
            summary = "List all clients",
            description = "Returns a list of all clients (admin and manager only)",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Clients successfully returned",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClientResponseDTO.class))),
                    @ApiResponse(responseCode = "204", description = "No clients found"),
                    @ApiResponse(responseCode = "403", description = "Access denied",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') OR hasRole('MANAGER')")
    public ResponseEntity<List<ClientResponseDTO>> getAllClient() {
        List<Client> clients = clientService.getAllClient();
        if(clients.isEmpty())
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok().body(ClientResponseDTO.toList(clients));
    }

    @Operation(
            summary = "List clients from authenticated seller",
            description = "Returns all clients associated with the authenticated seller",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Clients successfully returned",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClientResponseDTO.class))),
                    @ApiResponse(responseCode = "204", description = "No clients found"),
                    @ApiResponse(responseCode = "403", description = "Access denied",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @GetMapping("/me")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<List<ClientResponseDTO>> getMyClients(@AuthenticationPrincipal JwtUserDetails userDetails) {
        List<Client> clients = clientService.getMyClients(userDetails.getId());
        if (!clients.isEmpty())
            return ResponseEntity.ok().body(ClientResponseDTO.toList(clients));
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get client by ID",
            description = "Returns a specific client if it belongs to the authenticated user or user has permission",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Client successfully returned",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClientResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Client not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "400", description = "Client not available for this user",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Access denied",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> getClientById(@PathVariable Long id,
                                                           @AuthenticationPrincipal JwtUserDetails userDetails) {
        Client client = clientService.getClientById(id, userDetails.getId());
        return ResponseEntity.ok().body(ClientResponseDTO.toClientDto(client));
    }

    @Operation(
            summary = "Update client",
            description = "Updates a client if it belongs to the authenticated user",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Client successfully updated",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClientResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Client not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "422", description = "Invalid input fields",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "400", description = "Client not available for update for this user",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Access denied",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PutMapping
    public ResponseEntity<ClientResponseDTO> updateClient(@RequestBody ClientUpdateDTO clientDTO,
                                                          @AuthenticationPrincipal JwtUserDetails userDetails) {
        Client client = clientService.updateClient(ClientUpdateDTO.toClient(clientDTO), userDetails.getId());
        return ResponseEntity.ok().body(ClientResponseDTO.toClientDto(client));
    }

    @Operation(
            summary = "Delete client",
            description = "Deletes a client if it belongs to the authenticated user",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Client successfully deleted"),
                    @ApiResponse(responseCode = "404", description = "Client not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "422", description = "Invalid fields",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "400", description = "Client not available for deletion for this user",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Access denied",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id,
                                             @AuthenticationPrincipal JwtUserDetails userDetails){
        clientService.deleteClient(id, userDetails.getId());
        return ResponseEntity.ok().build();
    }

}
