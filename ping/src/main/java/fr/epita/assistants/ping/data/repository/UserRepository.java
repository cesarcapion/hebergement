package fr.epita.assistants.ping.data.repository;

import java.util.List;
import java.util.UUID;

import fr.epita.assistants.ping.data.model.UserModel;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class UserRepository  implements PanacheRepository<UserModel> {
    @Transactional
    public void addUser(UserModel user) {
        persist(user);
    }

    public boolean isAdmin(UUID id) {
        UserModel user = find("id", id).firstResult();

        if (user == null || user.getRole() == null) {
            return false;
        }

        return user.getRole().getName().equals("admin");
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

    @Transactional
    public void deleteUser(UserModel user) {
        PanacheRepository.super.delete(user);
    }

    @Transactional
    public void updateUser(UserModel updatedUser) {
        UserModel existingUser = findById(updatedUser.getId());
        if (existingUser != null) {
            existingUser.setLogin(updatedUser.getLogin());
            existingUser.setDisplayName(updatedUser.getDisplayName());
            existingUser.setPassword(updatedUser.getPassword());
            existingUser.setAvatar(updatedUser.getAvatar());
            existingUser.setRole(updatedUser.getRole());
            existingUser.setTickets(updatedUser.getTickets());
        }
    }
}