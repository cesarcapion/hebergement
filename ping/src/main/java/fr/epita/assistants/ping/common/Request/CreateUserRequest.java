package fr.epita.assistants.ping.common.Request;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class CreateUserRequest {
    public String login;
    public String password;
    public boolean isAdmin;
}
