package fr.epita.assistants.ping.domain.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import fr.epita.assistants.ping.data.model.UserModel;
import fr.epita.assistants.ping.data.repository.UserRepository;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;

public class UserService {
    @Inject
    UserRepository repository;

    public List<UserModel> getAll() {
        return repository.listAll();
    }

    public UserModel get(UUID id) {
        UserModel user = repository.findById(id);
        
        // error for existence of user 
        return user;
    }

    /*private void checkLogin(String login) {
        if (!login.matches("^[a-zA-Z0-9]+[._][a-zA-Z0-9]+$")) {

            // error 400
        }
    }*/

    private String generateDisplayName(String login) {
        String[] parts = login.split("[._]");
        
        StringBuilder result = new StringBuilder();
        for (String s : parts) {

            if (!s.isEmpty()) {
                String formatted = s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
                result.append(formatted).append(" ");
            }
        }
        return result.toString().trim();
    }

    @Transactional
    public UserModel create(UserModel input) {
        //checkLogin(input.getLogin());

        /*if (repository.findByLogin(input.getLogin()) != null)
        {
            // error 400
        }*/

        input.setDisplayName(generateDisplayName(input.getLogin()));
        repository.persist(input);
        return input;
    }

    @Transactional
    public UserModel update(UUID id, UserModel input) {
        UserModel user = get(id);

        user.setAvatar(input.getAvatar());
        user.setDisplayName(input.getDisplayName());

        return user;
    }

    @Transactional
    public void delete(UUID id) {
        UserModel user = get(id);
        repository.delete(user);
    }
}
