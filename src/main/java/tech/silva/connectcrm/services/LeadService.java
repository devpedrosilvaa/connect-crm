package tech.silva.connectcrm.services;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.silva.connectcrm.enums.Role;
import tech.silva.connectcrm.exceptions.EntityNotAvailableForViewException;
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

    public Lead saveLead(Lead lead, Long id){
        AppUser user = userRepository.findById(id).orElseThrow(
                () ->  {
                    throw new EntityNotFoundException(
                            String.format("User with Id= %s not found", id));
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

    public List<Lead> getMyLeads(Long id) {
        AppUser user = userRepository.findById(id).orElseThrow(
                () ->  {
                    throw new EntityNotFoundException(
                            String.format("User with Id= %s not found", id));
                }
        );
        return leadRepository.findAllByUser(user);
    }

    public Lead getLeadById(Long id, Long userId) {
        AppUser user = userRepository.findById(userId).orElseThrow(
                () ->  {
                    throw new EntityNotFoundException(
                            String.format("User with Id= %s not found", userId));
                }
        );
        Lead lead = leadRepository.findById(id).orElseThrow(
                () ->  {
                    throw new EntityNotFoundException(
                            String.format("Lead with Id= %s not found", id));
                }
        );
        if (!user.getRole().equals(Role.ROLE_SELLER))
            return lead;
        else if (!lead.getUser().equals(user))
            throw new EntityNotAvailableForViewException("This lead is not available for viewing by this user");
        return lead;
    }

    public Lead updateLead(Lead lead, Long id) {
        AppUser user = userRepository.findById(id).orElseThrow(
                () ->  {
                    throw new EntityNotFoundException(
                            String.format("User with Id= %s not found", id));
                }
        );
        Lead leadSaved = leadRepository.findById(lead.getId()).orElseThrow(
                () ->  {
                    throw new EntityNotFoundException(
                            String.format("Lead with Id= %s not found", lead.getId()));
                }
        );
        leadSaved.setName(lead.getName());
        leadSaved.setEmail(lead.getEmail());
        leadSaved.setPhone(lead.getPhone());
        leadSaved.setOrigin(lead.getOrigin());

        if (!leadSaved.getUser().equals(user))
            throw new EntityNotAvailableForViewException("This lead is not available for updating by this user");
        return leadRepository.save(leadSaved);
    }

    public void deleteLead(Long id, Long userId) {
        AppUser user = userRepository.findById(userId).orElseThrow(
                () ->  {
                    throw new EntityNotFoundException(
                            String.format("User with Id= %s not found", userId));
                }
        );
        Lead lead = leadRepository.findById(id).orElseThrow(
                () ->  {
                    throw new EntityNotFoundException(
                            String.format("Lead with Id= %s not found", id));
                }
        );
        if (!lead.getUser().equals(user))
            throw new EntityNotAvailableForViewException("This lead is not available for deleting by this user");
        leadRepository.delete(lead);
    }
}
