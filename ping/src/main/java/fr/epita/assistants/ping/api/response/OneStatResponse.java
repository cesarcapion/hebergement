package fr.epita.assistants.ping.api.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.time.Duration;
import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@With
public class OneStatResponse {
    public String mail;
    public long PendingTickets;
    public long ResolvedTickets;
    public long InProgressTickets;
    public String AverageAnswerTime;
}
