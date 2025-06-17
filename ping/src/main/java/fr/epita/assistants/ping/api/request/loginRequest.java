package fr.epita.assistants.ping.api.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class loginRequest {
    public String login;
    public String password;
}
