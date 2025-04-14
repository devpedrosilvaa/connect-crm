package tech.silva.connectcrm.services;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.silva.connectcrm.exceptions.UniqueUserViolationException;
import tech.silva.connectcrm.models.AppUser;
import tech.silva.connectcrm.models.Lead;
import tech.silva.connectcrm.repositories.ILeadRepository;
import tech.silva.connectcrm.repositories.IUserRepository;

import java.util.List;

@Service
@Transactional
public class LeadService {

    private final ILeadRepository leadRepository;
    private final IUserRepository userRepository;

    public LeadService(ILeadRepository leadRepository, IUserRepository userRepository) {
        this.leadRepository = leadRepository;
        this.userRepository = userRepository;
    }

    public Lead saveLead(Lead lead){
        AppUser user = userRepository.findById(lead.getUser().getId()).orElseThrow(
                () ->  {
                    throw new EntityNotFoundException(
                            String.format("User with Id= %s not found", lead.getUser().getId()));
                }
        );
        if (leadRepository.findByEmail(lead.getEmail()).isPresent())
            throw new UniqueUserViolationException(
                    String.format("Lead with email: %s already registered. Try again!", lead.getEmail()));

        lead.setUser(user);
        return leadRepository.save(lead);
    }

    public List<Lead> listAllLeads(){
        return leadRepository.findAll();
    }


}
