package fr.epita.assistants.ping.api.response;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Data
public class FAQResponse {
    public UUID id;
    public String question;
    public String response;
}
