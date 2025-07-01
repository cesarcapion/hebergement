package fr.epita.assistants.ping.api.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@AllArgsConstructor
@NoArgsConstructor
@With
@Data
public class PasswordRequest {
    public String token;
    public String password;
}
