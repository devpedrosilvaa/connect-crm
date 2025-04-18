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
import tech.silva.connectcrm.dtos.lead.LeadUpdateDTO;
import tech.silva.connectcrm.dtos.lead.LeadCreateDTO;
import tech.silva.connectcrm.dtos.lead.LeadResponseDTO;
import tech.silva.connectcrm.exceptions.ErrorMessage;
import tech.silva.connectcrm.jwt.JwtUserDetails;
import tech.silva.connectcrm.models.Lead;
import tech.silva.connectcrm.services.LeadService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/leads")
@Tag(name = "Leads", description = "Operations related to lead management")
public class LeadController {

    private final LeadService leadService;

    public LeadController(LeadService leadService) {
        this.leadService = leadService;
    }

    @Operation(
            summary = "Register a new lead",
            description = "Creates a new lead associated with the authenticated user",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Lead successfully created",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = LeadResponseDTO.class))),
                    @ApiResponse(responseCode = "422", description = "Invalid input fields or lead already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Access denied",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PostMapping
    public ResponseEntity<LeadResponseDTO> saveLead(@RequestBody @Valid LeadCreateDTO leadDTO,
                                                    @AuthenticationPrincipal JwtUserDetails userDetails) {
        Lead lead = leadService.saveLead(LeadCreateDTO.toLead(leadDTO), userDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(LeadResponseDTO.toLeadDto(lead));
    }

    @Operation(
            summary = "List all leads",
            description = "Returns a list of all leads (admin and manager only)",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Leads successfully returned",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = LeadResponseDTO.class))),
                    @ApiResponse(responseCode = "204", description = "No leads found"),
                    @ApiResponse(responseCode = "403", description = "Access denied",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') OR hasRole('MANAGER')")
    public ResponseEntity<List<LeadResponseDTO>> getAll() {
        List<Lead> leads = leadService.listAllLeads();
        if (!leads.isEmpty())
            return ResponseEntity.ok().body(LeadResponseDTO.toList(leads));
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "List leads from authenticated seller",
            description = "Returns all leads associated with the authenticated seller",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Leads successfully returned",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = LeadResponseDTO.class))),
                    @ApiResponse(responseCode = "204", description = "No leads found"),
                    @ApiResponse(responseCode = "403", description = "Access denied",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @GetMapping("/me")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<List<LeadResponseDTO>> getMyLeads(@AuthenticationPrincipal JwtUserDetails userDetails) {
        List<Lead> leads = leadService.getMyLeads(userDetails.getId());
        if (!leads.isEmpty())
            return ResponseEntity.ok().body(LeadResponseDTO.toList(leads));
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get lead by ID",
            description = "Returns a specific lead if it belongs to the authenticated user or user has permission",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lead successfully returned",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = LeadResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Lead not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "400", description = "Lead not available for this user",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Access denied",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<LeadResponseDTO> getLeadsById(@PathVariable Long id,
                                                        @AuthenticationPrincipal JwtUserDetails userDetails) {
        Lead lead = leadService.getLeadById(id, userDetails.getId());
        return ResponseEntity.ok().body(LeadResponseDTO.toLeadDto(lead));
    }

    @Operation(
            summary = "Update lead",
            description = "Updates a lead if it belongs to the authenticated user",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lead successfully updated",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = LeadResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Lead not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "422", description = "Invalid input fields",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "400", description = "Lead not available for update for this user",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Access denied",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PutMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<LeadResponseDTO> updateLead(@RequestBody @Valid LeadUpdateDTO leadDTO,
                                                      @AuthenticationPrincipal JwtUserDetails userDetails) {
        Lead lead = leadService.updateLead(LeadUpdateDTO.toLead(leadDTO), userDetails.getId());
        return ResponseEntity.ok().body(LeadResponseDTO.toLeadDto(lead));
    }

    @Operation(
            summary = "Delete lead",
            description = "Deletes a lead if it belongs to the authenticated user",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lead successfully deleted"),
                    @ApiResponse(responseCode = "404", description = "Lead not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "400", description = "Lead not available for deletion for this user",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Access denied",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Void> deleteLead(@PathVariable Long id,
                                           @AuthenticationPrincipal JwtUserDetails userDetails) {
        leadService.deleteLead(id, userDetails.getId());
        return ResponseEntity.ok().build();
    }

}
