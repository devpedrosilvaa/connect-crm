package tech.silva.connectcrm.services;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.silva.connectcrm.exceptions.UniqueUserViolationException;
import tech.silva.connectcrm.models.AppUser;
import tech.silva.connectcrm.models.Client;
import tech.silva.connectcrm.repositories.IClientRepository;
import tech.silva.connectcrm.repositories.IUserRepository;

@Service
@Transactional
public class ClientService {

    private final IClientRepository clientRepository;
    private final IUserRepository userRepository;

    public ClientService(IClientRepository clientRepository, IUserRepository userRepository) {
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
    }

    public Client saveClient(Client client, Long idUser){
        AppUser user = userRepository.findById(idUser).orElseThrow(
                () ->  {
                    throw new EntityNotFoundException(
                            String.format("User with Id= %s not found", idUser));
                }
        );
        if (clientRepository.findByEmail(client.getEmail()).isPresent())
            throw new UniqueUserViolationException(
                    String.format("Client with email: %s already registered. Try again!", client.getEmail()));

        client.setUser(user);
        return clientRepository.save(client);
    }
}
