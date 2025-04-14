package tech.silva.connectcrm.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tech.silva.connectcrm.dtos.lead.LeadUpdateDTO;
import tech.silva.connectcrm.dtos.lead.LeadCreateDTO;
import tech.silva.connectcrm.dtos.lead.LeadResponseDTO;
import tech.silva.connectcrm.jwt.JwtUserDetails;
import tech.silva.connectcrm.models.Lead;
import tech.silva.connectcrm.services.LeadService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/leads")
public class LeadController {

    private final LeadService leadService;

    public LeadController(LeadService leadService) {
        this.leadService = leadService;
    }

    @PostMapping
    public ResponseEntity<LeadResponseDTO> saveLead(@RequestBody @Valid LeadCreateDTO leadDTO,
                                                        @AuthenticationPrincipal JwtUserDetails userDetails){
        Lead lead = leadService.saveLead(LeadCreateDTO.toLead(leadDTO), userDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(LeadResponseDTO.toLeadDto(lead));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') OR hasRole('MANAGER')")
    public ResponseEntity<List<LeadResponseDTO>> getAll(){
        List<Lead> leads = leadService.listAllLeads();
        if (!leads.isEmpty())
            return ResponseEntity.ok().body(LeadResponseDTO.toList(leads));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<List<LeadResponseDTO>> getMyLeads(@AuthenticationPrincipal JwtUserDetails userDetails){
        List<Lead> leads = leadService.getMyLeads(userDetails.getId());
        if (!leads.isEmpty())
            return ResponseEntity.ok().body(LeadResponseDTO.toList(leads));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeadResponseDTO> getLeadsById(@PathVariable Long id,
                                                          @AuthenticationPrincipal JwtUserDetails userDetails){
        Lead lead = leadService.getLeadById(id, userDetails.getId());
        return ResponseEntity.ok().body(LeadResponseDTO.toLeadDto(lead));
    }

    @PutMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<LeadResponseDTO> updateLead(@RequestBody @Valid LeadUpdateDTO leadDTO,
                                                        @AuthenticationPrincipal JwtUserDetails userDetails){
        Lead lead = leadService.updateLead(LeadUpdateDTO.toLead(leadDTO), userDetails.getId());
        return ResponseEntity.ok().body(LeadResponseDTO.toLeadDto(lead));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Void> deleteLead(@PathVariable Long id,
                                            @AuthenticationPrincipal JwtUserDetails userDetails){
        leadService.deleteLead(id, userDetails.getId());
        return ResponseEntity.ok().build();
    }
}
