package fr.epita.assistants.ping.api.response;

import fr.epita.assistants.ping.utils.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@With
public class TicketHistoryResponse {
    public Long id;

    public UUID ticketId;

    public UUID interactedBy;

    public String contentPath;

    public String resourcePath;

    public TicketStatus ticketStatus;

    public LocalDateTime interactedOn;
}
