package fr.epita.assistants.ping.common.Response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Data
public class UserResponse {
    public UUID id;
    public String login;
    public String displayName;
    public boolean isAdmin;
    public String avatar;
}
