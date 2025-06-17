package fr.epita.assistants.ping.data.repository;

import java.util.List;
import java.util.UUID;

import fr.epita.assistants.ping.data.model.UserModel;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserRepository  implements PanacheRepository<UserModel> {
    public void addUser(UserModel user) {
        persist(user);
    }

    public UserModel findById(UUID id) {
        return find("id", id).firstResult();
    }

    public UserModel findByLogin(String login) {
        return find("login", login).firstResult();
    }

    public List<UserModel> listAll() {
        return findAll().stream().toList();
    }

    public void delete(UserModel user) {
        delete(user);
    }
}