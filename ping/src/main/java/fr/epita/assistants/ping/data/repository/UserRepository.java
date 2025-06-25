package fr.epita.assistants.ping.data.repository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
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
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
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

    public UserModel findByLogin(String mail) {
        return find("mail", mail).firstResult();
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
            existingUser.setMail(updatedUser.getMail());
            existingUser.setDisplayName(updatedUser.getDisplayName());
            existingUser.setPassword(hashPassword(updatedUser.getPassword()));
            existingUser.setAvatar(updatedUser.getAvatar());
            existingUser.setRole(updatedUser.getRole());
            existingUser.setTickets(updatedUser.getTickets());
        }
    }
}