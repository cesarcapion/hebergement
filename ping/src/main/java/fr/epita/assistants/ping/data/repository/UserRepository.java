package fr.epita.assistants.ping.data.repository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;

import fr.epita.assistants.ping.api.request.PasswordRequest;
import fr.epita.assistants.ping.api.request.UserUpdateRequest;
import fr.epita.assistants.ping.api.response.UserResponse;
import fr.epita.assistants.ping.data.model.RoleModel;
import fr.epita.assistants.ping.data.model.UserModel;
import fr.epita.assistants.ping.errors.Exceptions.InvalidException;
import fr.epita.assistants.ping.utils.DefaultRoles;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class UserRepository  implements PanacheRepository<UserModel> {
    @Inject
    RoleRepository roleRepository;
    @Transactional
    public void addUser(UserModel user) {
        persist(user);
    }
    public String loginToName(String mail) {
        String loginPart = mail.split("@")[0];

        String[] parts = loginPart.split("[._]");
        StringBuilder displayName = new StringBuilder();

        for (int i = 0; i < parts.length; i++) {
            if (parts[i].isEmpty()) continue;

            String part = parts[i];
            displayName.append(Character.toUpperCase(part.charAt(0)))
                    .append(part.substring(1));

            if (i < parts.length - 1) {
                displayName.append(" ");
            }
        }

        return displayName.toString();
    }

    @Transactional
    public void setResetToken(String mail, String token) {
        update("resetToken = ?1 where mail = ?2", token, mail);

    }
    @Transactional
    public void setPassword(String password, String token) {
        update("password = ?1 where resetToken = ?2", password, token);

    }
    public UserModel findByResetToken(String token) {
        return find("resetToken", token).firstResult();
    }
    public UserModel getUserByResetToken(String token) {
        return find("resetToken", token).firstResult();
    }

    @Transactional
    public UserResponse createUser(String mail, String password) {
        UserModel newUser = new UserModel();
        newUser.setDisplayName(loginToName(mail));
        newUser.setAvatar("");
        newUser.setMail(mail);
        newUser.setPassword(hashPassword(password));
        RoleModel role = roleRepository.getRoleById(DefaultRoles.getUserRoleId());
        newUser.setRole(role);
        persist(newUser);
        return new UserResponse(newUser.getId(),newUser.getMail(),newUser.getDisplayName(), Objects.equals(newUser.getRole().getName(), "admin"),newUser.getAvatar(), newUser.getRole().getId());

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
    public boolean isUser(String mail) {
        UserModel user = findByLogin(mail);

        if (user == null || user.getRole() == null) {
            return false;
        }

        return user.getRole().getName().equals("user");
    }
    public List<String> findAllNonUserMails() {
        return getEntityManager()
                .createQuery("SELECT u.mail FROM UserModel u WHERE u.role.name <> 'user'", String.class)
                .getResultList();
    }

    public UserModel findById(UUID id) {
        return find("id", id).firstResult();
    }

    public RoleModel findRoleById(UUID id) {
        return find("id", id).firstResult().getRole();
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
    public void updateUser(UserModel updatedUser, UserUpdateRequest input) {
        if (updatedUser != null)
        {
        if (input.password !=null &&!input.password.isBlank())
            updatedUser.setPassword(hashPassword(input.password));
        if (input.displayName != null && !input.displayName.isBlank())
            updatedUser.setDisplayName(input.displayName);
        if (input.avatar !=null && !input.avatar.isBlank())
            updatedUser.setAvatar(input.avatar);
        }
    }
}