package fr.epita.assistants.ping.api.request;
import lombok.AllArgsConstructor;
import lombok.Data;

public class CreateUserRequest {
    public String mail;
    public String password;
    public boolean isAdmin;
}
