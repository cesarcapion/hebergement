package fr.epita.assistants.ping.domain.service;

import java.time.Instant;
import java.util.*;

import fr.epita.assistants.ping.api.request.CreateUserRequest;
import fr.epita.assistants.ping.api.response.UserResponse;
import fr.epita.assistants.ping.api.response.LoginResponse;
import fr.epita.assistants.ping.data.model.UserModel;
import fr.epita.assistants.ping.data.repository.UserRepository;
import fr.epita.assistants.ping.errors.Exceptions.AlreadyExistException;
import fr.epita.assistants.ping.errors.Exceptions.BadInfosException;
import fr.epita.assistants.ping.errors.Exceptions.InvalidException;
import fr.epita.assistants.ping.errors.Exceptions.UserException;
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
    @ConfigProperty(name= "KEY", defaultValue = "remy") static String key;


    private boolean checkLogin(String login, String password) {
        return login.matches("^[a-zA-Z0-9]+[._][a-zA-Z0-9]+$") && password.matches("[a-zA-Z]+");

    }

    public static String generateToken(String userId, boolean isAdmin) {
        return Jwt.claims()
                .issuer("https://mon-app")
                .subject("user123")
                .groups(Set.of("user", "admin")) // rôles
                .claim("email", "user@example.com")
                .signWithSecret("ma-super-cle-ultra-secrète-12345678901234567890");
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
                    part.substring(1).toLowerCase()
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
        return new LoginResponse(generateToken(login,repository.findByLogin(login).getIsAdmin()));
    }

    public LoginResponse refreshToken(String login) throws UserException {

        if (repository.findByLogin(login) == null)
        {
            throw new UserException("login invalid");
        }
        return new LoginResponse(generateToken(login,repository.findByLogin(login).getIsAdmin()));
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
