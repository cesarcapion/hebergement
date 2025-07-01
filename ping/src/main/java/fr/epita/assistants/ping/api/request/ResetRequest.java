package fr.epita.assistants.ping.api.request;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

@AllArgsConstructor
@NoArgsConstructor
@With
public class ResetRequest {
    public String mail;
}
