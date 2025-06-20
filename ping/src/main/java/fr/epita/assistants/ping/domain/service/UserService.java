package fr.epita.assistants.ping.domain.service;

import java.io.Console;
import java.time.Instant;
import java.util.*;

import fr.epita.assistants.ping.api.request.CreateUserRequest;
import fr.epita.assistants.ping.api.request.UserUpdateRequest;
import fr.epita.assistants.ping.api.response.UserResponse;
import fr.epita.assistants.ping.api.response.LoginResponse;
import fr.epita.assistants.ping.data.model.UserModel;
import fr.epita.assistants.ping.data.repository.ProjectMembersRepository;
import fr.epita.assistants.ping.data.repository.UserRepository;
import fr.epita.assistants.ping.errors.Exceptions.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import io.smallrye.jwt.build.Jwt;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Duration;

@ApplicationScoped
public class UserService {
    @Inject
    UserRepository repository;


    @Inject ProjectMembersService pmService;
    @ConfigProperty(name= "KEY", defaultValue = "remy") String key;

    @Inject ProjectService projectService;

    private boolean checkLogin(String login, String password) {
        return login.matches("^[a-zA-Z0-9]+[._][a-zA-Z0-9]+$") /*&& password.matches("[a-zA-Z]+")*/;

    }

    public static String generateToken(UUID userId, boolean isAdmin) {
        System.out.println(userId + " : " + isAdmin);
        return Jwt.claim("sub", userId.toString())
                .claim("groups", isAdmin ? "admin" : "user")
                .claim("iat", Instant.now().getEpochSecond())
                .issuer("http://mon-app.epita.fr")
                .expiresIn(Duration.ofHours(1))
                .sign();
    }

    public String loginToName(String login) {
        String[] parts = login.split("[._]");
        StringBuilder displayName = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].isEmpty()) continue;
            String part = parts[i];
            displayName.append(
                    Character.toUpperCase(part.charAt(0))
            ).append(
                    part.substring(1)
            );
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
        if (!checkLogin(input.login, input.password))
            throw new InvalidException("login or password not valid"); // 400

        if (repository.findByLogin(input.login) != null)
        {
            throw new AlreadyExistException("the login is already used"); // 409
        }
        UserModel newUser = new UserModel();
        newUser.setDisplayName(loginToName(input.login));
        newUser.setAvatar("");
        newUser.setIsAdmin(input.isAdmin);
        newUser.setLogin(input.login);
        newUser.setPassword(input.password);

        repository.addUser(newUser);
        return new UserResponse(newUser.getId(),newUser.getLogin(),newUser.getDisplayName(),newUser.getIsAdmin(),newUser.getAvatar());
    }

    /*
        get all users
     */
    public UserResponse[] getAllUsers()
    {
        List<UserModel> list = repository.listAll();
        List<UserResponse> response = new ArrayList<>();
        for (UserModel user : list) {
            UserResponse element = new UserResponse(user.getId(),user.getLogin(),user.getDisplayName(),user.getIsAdmin(),user.getAvatar());
            response.add(element);
        }
        return response.toArray(new UserResponse[0]); // 200
    }

    public LoginResponse loginUser(String login, String password) throws InvalidException, BadInfosException {
        if (login == null || password==null)
        {
            throw new InvalidException("login or password is null");
        }
        if (repository.findByLogin(login) == null || !Objects.equals(repository.findByLogin(login).getPassword(), password))
        {
            throw new BadInfosException("password or login invalid");
        }
        return new LoginResponse(generateToken(repository.findByLogin(login).getId(),repository.findByLogin(login).getIsAdmin()));
    }

    public LoginResponse refreshToken(UUID id) throws UserException {
        String login = repository.findById(id).getLogin();
        if (repository.findByLogin(login) == null)
        {
            System.out.println(login);
            throw new UserException("login invalid");
        }
        return new LoginResponse(generateToken(repository.findByLogin(login).getId(),repository.findByLogin(login).getIsAdmin()));
    }


    public UserModel get(UUID id) {
        UserModel user = repository.findById(id);
        
        // error for existence of user 
        return user;
    }



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



    public UserResponse update(UUID userId, UUID userToUpdateId, UserUpdateRequest input) throws NotAuthorizedException, UserException {
        if (repository.findById(userToUpdateId) == null)
            throw new UserException("utilisateur introuvable"); // 404
        if (!repository.findById(userId).getIsAdmin() && !Objects.equals(repository.findById(userId).getLogin(), repository.findById(userToUpdateId).getLogin()))
            throw new NotAuthorizedException("l'utilisateur n'a pas les droits"); // 403
        UserModel user = repository.findById(userToUpdateId);
        if (input.password !=null &&!input.password.isBlank())
            user.setPassword(input.password);
        if (input.displayName != null && !input.displayName.isBlank())
            user.setDisplayName(input.displayName);
        user.setAvatar(input.avatar);
        repository.updateUser(user);
        return new UserResponse(user.getId(),user.getLogin(),user.getDisplayName(),user.getIsAdmin(),user.getAvatar());
    }
    public UserResponse get(UUID userId, UUID userToUpdateId) throws NotAuthorizedException, UserException {
        if (repository.findById(userToUpdateId) == null)
            throw new UserException("utilisateur introuvable"); // 404

        if (!repository.findById(userId).getIsAdmin() && !Objects.equals(repository.findById(userId).getLogin(), repository.findById(userToUpdateId).getLogin()))
            throw new NotAuthorizedException("l'utilisateur n'a pas les droits"); // 403

        UserModel user = repository.findById(userToUpdateId);
        return new UserResponse(user.getId(),user.getLogin(),user.getDisplayName(),user.getIsAdmin(),user.getAvatar());
    }

    @Transactional
    public void delete(UUID userToRemoveid, UUID userRemoverID) throws UserException, NotAuthorizedException {
        if (repository.findById(userToRemoveid) == null)
            throw new UserException("utilisateur introuvable"); // 404
        if (!projectService.buildGetProjectsResponse(userToRemoveid.toString(),true).isEmpty() || !repository.findById(userRemoverID).getIsAdmin())
            throw new NotAuthorizedException("L'utilisateur a un/des projets"); //403
        repository.deleteUser(repository.findById(userToRemoveid));
        pmService.deleteFromAllProjects(userToRemoveid);
    }
}
