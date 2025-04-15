package tech.silva.connectcrm.services;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.silva.connectcrm.enums.Role;
import tech.silva.connectcrm.exceptions.EntityNotAvailableForViewException;
import tech.silva.connectcrm.exceptions.UniqueUserViolationException;
import tech.silva.connectcrm.models.AppUser;
import tech.silva.connectcrm.models.Client;
import tech.silva.connectcrm.repositories.IClientRepository;
import tech.silva.connectcrm.repositories.IUserRepository;

import java.util.List;

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

    public List<Client> getAllClient(){
        return clientRepository.findAll();
    }


    public List<Client> getMyClients(Long id) {
        AppUser user = userRepository.findById(id).orElseThrow(
                () ->  {
                    throw new EntityNotFoundException(
                            String.format("User with Id= %s not found", id));
                }
        );

        return clientRepository.findAllByUser(user);
    }

    public Client getClientById(Long id, Long userId) {
        AppUser user = userRepository.findById(userId).orElseThrow(
                () ->  {
                    throw new EntityNotFoundException(
                            String.format("User with Id= %s not found", userId));
                }
        );
        Client client = clientRepository.findById(id).orElseThrow(
                () ->  {
                    throw new EntityNotFoundException(
                            String.format("client with Id= %s not found", id));
                }
        );
        if (!user.getRole().equals(Role.ROLE_SELLER))
            return client;
        else if (!client.getUser().equals(user))
            throw new EntityNotAvailableForViewException("This client is not available for viewing by this user");
        return client;
    }

    public Client updateClient(Client client, Long id){
        AppUser user = userRepository.findById(id).orElseThrow(
                () ->  {
                    throw new EntityNotFoundException(
                            String.format("User with Id= %s not found", id));
                }
        );

        Client clientSaved = clientRepository.findById(client.getId()).orElseThrow(
                () ->  {
                    throw new EntityNotFoundException(
                            String.format("Client with Id= %s not found", client.getId()));
                }
        );

        clientSaved.setName(client.getName());
        clientSaved.setPhone(client.getPhone());
        clientSaved.setDocument(client.getDocument());
        clientSaved.setAddress(client.getAddress());

        if (!clientSaved.getUser().equals(user))
            throw new EntityNotAvailableForViewException("This client is not available for viewing by this user");
        return clientRepository.save(clientSaved);
    }
}
