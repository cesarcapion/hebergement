package fr.epita.assistants.ping.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Data
public class UserResponse {
    public UUID id;
    public String mail;
    public String displayName;
    public boolean isAdmin;
    public String avatar;
    public Long roleId;
    public String created;
    public String role;
}
