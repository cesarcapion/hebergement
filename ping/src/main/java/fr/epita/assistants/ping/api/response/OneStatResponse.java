package fr.epita.assistants.ping.api.response;

import java.time.LocalDateTime;

public class OneStatResponse {
    public long PendingTickets;
    public long ResolvedTickets;
    public long InProgressTickets;
    public LocalDateTime AverageAnswerTime;
}
