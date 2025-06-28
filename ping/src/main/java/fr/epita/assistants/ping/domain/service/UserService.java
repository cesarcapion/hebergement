package fr.epita.assistants.ping.domain.service;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

import java.time.Instant;
import java.util.*;

import fr.epita.assistants.ping.api.request.CreateUserRequest;
import fr.epita.assistants.ping.api.request.UserUpdateRequest;
import fr.epita.assistants.ping.api.response.UserResponse;
import fr.epita.assistants.ping.api.response.LoginResponse;
import fr.epita.assistants.ping.data.model.RoleModel;
import fr.epita.assistants.ping.data.model.UserModel;
import fr.epita.assistants.ping.data.repository.UserRepository;
import fr.epita.assistants.ping.errors.Exceptions.*;
import fr.epita.assistants.ping.utils.DefaultRoles;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import io.smallrye.jwt.build.Jwt;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Duration;

import static org.postgresql.util.MD5Digest.bytesToHex;

@ApplicationScoped
public class UserService {
    @Inject
    UserRepository repository;
    @Inject
    RoleService roleService;


    @ConfigProperty(name= "KEY", defaultValue = "remy") String key;

    @Inject
    TicketService ticketService;

    public boolean isAdmin(UUID uuid)
    {
        return repository.isAdmin(uuid);
    }
    public boolean isUser(String mail)
    {
        return repository.isUser(mail);
    }
    private boolean checkLogin(String mail) {
        return mail.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    }
    private boolean checkPassword(String password) {
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z\\d]).{12,}$");
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

    public boolean mailExist(String mail)
    {
        return repository.findByLogin(mail) != null;
    }

    public String generateToken(UUID userId, boolean isAdmin) {
        System.out.println(userId + " : " + isAdmin);
        return Jwt.subject(userId.toString())
                .groups(Set.of(repository.findRoleById(userId).getName()))
                .issuer("http://mon-app.epita.fr")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .sign();
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


    /*
        create a new user
     */
    public UserResponse create(CreateUserRequest input) throws InvalidException, AlreadyExistException {
        if (!checkLogin(input.mail))
            throw new InvalidException("email not valid"); // 400
        if (!checkPassword(input.password))
            throw new InvalidException("password not valid"); // 400

        if (repository.findByLogin(input.mail) != null)
        {
            throw new AlreadyExistException("the mail is already used"); // 409
        }
        UserModel newUser = new UserModel();
        newUser.setDisplayName(loginToName(input.mail));
        newUser.setAvatar("");
        newUser.setMail(input.mail);
        newUser.setPassword(hashPassword(input.password));
        RoleModel role;
        if (input.isAdmin) {
//            role = roleService.findByName("admin");
            role = roleService.getRoleById(DefaultRoles.getAdminRoleId());
        }
        else
        {
//            role = roleService.findByName("user");
            role = roleService.getRoleById(DefaultRoles.getUserRoleId());
        }
        newUser.setRole(role);

        repository.addUser(newUser);
        return new UserResponse(newUser.getId(),newUser.getMail(),newUser.getDisplayName(),Objects.equals(newUser.getRole().getName(), "admin"),newUser.getAvatar());
    }

    /*
    * to create a new account if we don't have any
    */
    public UserResponse createNewAccount(CreateUserRequest input) throws InvalidException, AlreadyExistException {
        input.isAdmin = false;
        if (!checkLogin(input.mail))
            throw new InvalidException("email not valid"); // 400
        if (!checkPassword(input.password))
            throw new InvalidException("password not valid"); // 400

        if (repository.findByLogin(input.mail) != null)
        {
            throw new AlreadyExistException("the mail is already used"); // 409
        }
        UserModel newUser = new UserModel();
        newUser.setDisplayName(loginToName(input.mail));
        newUser.setAvatar("");
        newUser.setMail(input.mail);
        newUser.setPassword(hashPassword(input.password));
//        RoleModel role =roleService.findByName("user");
        RoleModel role = roleService.getRoleById(DefaultRoles.getUserRoleId());
        newUser.setRole(role);

        repository.addUser(newUser);
        return new UserResponse(newUser.getId(),newUser.getMail(),newUser.getDisplayName(),Objects.equals(newUser.getRole().getName(), "admin"),newUser.getAvatar());
    }

    /*
        get all users
     */
    public UserResponse[] getAllUsers()
    {
        List<UserModel> list = repository.listAll();
        List<UserResponse> response = new ArrayList<>();
        for (UserModel user : list) {
            UserResponse element = new UserResponse(user.getId(),user.getMail(),user.getDisplayName(),Objects.equals(user.getRole().getName(), "admin"),user.getAvatar());
            response.add(element);
        }
        return response.toArray(new UserResponse[0]); // 200
    }

    public LoginResponse loginUser(String mail, String password) throws InvalidException, BadInfosException {
        if (mail == null || password==null)
        {
            throw new InvalidException("mail or password is null");
        }
        if (repository.findByLogin(mail) == null || !Objects.equals(repository.findByLogin(mail).getPassword(), hashPassword(password)))
        {
            throw new BadInfosException("password or mail invalid");
        }
        return new LoginResponse(generateToken(repository.findByLogin(mail).getId(),Objects.equals(repository.findByLogin(mail).getRole().getName(), "admin") ));
    }

    public LoginResponse refreshToken(UUID id) throws UserException {
        String mail = repository.findById(id).getMail();
        if (repository.findByLogin(mail) == null)
        {
            System.out.println(mail);
            throw new UserException("mail invalid");
        }
        return new LoginResponse(generateToken(repository.findByLogin(mail).getId(),Objects.equals(repository.findByLogin(mail).getRole().getName(), "admin") ));
    }


    public UserModel get(UUID id) {
        UserModel user = repository.findById(id);
        
        // error for existence of user 
        return user;
    }



    private String generateDisplayName(String mail) {
        String[] parts = mail.split("[._]");
        
        StringBuilder result = new StringBuilder();
        for (String s : parts) {

            if (!s.isEmpty()) {
                String formatted = s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
                result.append(formatted).append(" ");
            }
        }
        return result.toString().trim();
    }



    public UserResponse update(UUID userId, UUID userToUpdateId, UserUpdateRequest input) throws NotAuthorizedException, UserException {
        if (repository.findById(userToUpdateId) == null)
            throw new UserException("utilisateur introuvable"); // 404
        if (!Objects.equals(repository.findById(userId).getRole().getName(), "admin") && !Objects.equals(repository.findById(userId).getMail(), repository.findById(userToUpdateId).getMail()))
            throw new NotAuthorizedException("l'utilisateur n'a pas les droits"); // 403
        UserModel user = repository.findById(userToUpdateId);

        repository.updateUser(user,input);
        return new UserResponse(user.getId(),user.getMail(),user.getDisplayName(),Objects.equals(user.getRole().getName(), "admin"),user.getAvatar());
    }
    public UserResponse get(UUID userId, UUID userToUpdateId) throws NotAuthorizedException, UserException {
        if (repository.findById(userToUpdateId) == null)
            throw new UserException("utilisateur introuvable"); // 404

        if (!Objects.equals(repository.findById(userId).getRole().getName(), "admin") && !Objects.equals(repository.findById(userId).getMail(), repository.findById(userToUpdateId).getMail()))
            throw new NotAuthorizedException("l'utilisateur n'a pas les droits"); // 403

        UserModel user = repository.findById(userToUpdateId);
        return new UserResponse(user.getId(),user.getMail(),user.getDisplayName(),Objects.equals(user.getRole().getName(), "admin"),user.getAvatar());
    }

    @Transactional
    public void delete(UUID userToRemoveid, UUID userRemoverID) throws UserException, NotAuthorizedException {
        if (repository.findById(userToRemoveid) == null)
            throw new UserException("utilisateur introuvable"); // 404
//        if (!ticketService.buildGetTicketsResponse(userToRemoveid.toString(),true).isEmpty() || !Objects.equals(repository.findById(userRemoverID).getRole().getName(), "admin") )
        if (ticketService.ownsProjects(userToRemoveid) || !Objects.equals(repository.findById(userRemoverID).getRole().getName(), "admin") )
            throw new NotAuthorizedException("L'utilisateur a un/des projets"); //403
        ticketService.deleteFromAllProjects(userToRemoveid);
        repository.deleteUser(repository.findById(userToRemoveid));
//        pmService.deleteFromAllProjects(userToRemoveid);
    }
}
