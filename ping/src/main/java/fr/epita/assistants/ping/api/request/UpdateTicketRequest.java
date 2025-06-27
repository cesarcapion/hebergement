package fr.epita.assistants.ping.api.request;

import fr.epita.assistants.ping.utils.TicketStatus;

public class UpdateTicketRequest {
    public String subject;
    public String newOwnerId;
    public TicketStatus ticketStatus;
    public Long newTopicId;
}
