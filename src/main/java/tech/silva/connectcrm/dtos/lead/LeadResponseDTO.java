package tech.silva.connectcrm.dtos.lead;

import tech.silva.connectcrm.dtos.user.UserResponseDTO;
import tech.silva.connectcrm.enums.StatusLead;
import tech.silva.connectcrm.models.Lead;

import java.util.List;
import java.util.stream.Collectors;

public record LeadResponseDTO(
        Long id,
        String name,
        String email,
        String phone,
        String origin,
        StatusLead status,
        UserResponseDTO user
) {
    public static LeadResponseDTO toLeadDto(Lead lead){
        return new LeadResponseDTO(
                lead.getId(),
                lead.getName(),
                lead.getEmail(),
                lead.getPhone(),
                lead.getOrigin(),
                lead.getStatus(),
                UserResponseDTO.toUserDto(lead.getUser())
        );
    }

    public static List<LeadResponseDTO> toList(List<Lead> leads){
        return leads.stream().
                map(lead -> toLeadDto(lead)).collect(Collectors.toList());
    }
}
