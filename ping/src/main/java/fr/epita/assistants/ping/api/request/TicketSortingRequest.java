package fr.epita.assistants.ping.api.request;

import fr.epita.assistants.ping.utils.TicketSortingStrategy;
import fr.epita.assistants.ping.utils.TicketStatus;

public class TicketSortingRequest {
    public TicketStatus filteringStrategy;

    public TicketSortingStrategy sortingStrategy;
    public boolean ascending;
}
