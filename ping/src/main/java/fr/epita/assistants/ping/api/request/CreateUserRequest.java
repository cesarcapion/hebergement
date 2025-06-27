package fr.epita.assistants.ping.api.request;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@AllArgsConstructor
@NoArgsConstructor
@With
public class CreateUserRequest {
    public String mail;
    public String password;
    public boolean isAdmin;
}
