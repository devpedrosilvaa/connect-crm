package tech.silva.connectcrm.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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


}
