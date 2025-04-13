package tech.silva.connectcrm.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.silva.connectcrm.dtos.lead.LeadCreateDTO;
import tech.silva.connectcrm.dtos.lead.LeadResponseDTO;
import tech.silva.connectcrm.models.Lead;
import tech.silva.connectcrm.services.LeadService;

@RestController
@RequestMapping("/api/v1/leads")
public class LeadController {

    private final LeadService leadService;

    public LeadController(LeadService leadService) {
        this.leadService = leadService;
    }

    @PostMapping
    public ResponseEntity<LeadResponseDTO> saveLead(@RequestBody @Valid LeadCreateDTO leadDTO){
        Lead lead = leadService.saveLead(LeadCreateDTO.toLead(leadDTO));
        return ResponseEntity.status(HttpStatus.CREATED).body(LeadResponseDTO.toLeadDto(lead));
    }
}
