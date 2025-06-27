package fr.epita.assistants.ping.api.response;

import fr.epita.assistants.ping.utils.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.With;

import java.time.LocalDateTime;
import java.util.ArrayList;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@With
public class TicketResponse {
    public String id;
    public String name;

    public ArrayList<UserInfoResponse> members;

    public UserInfoResponse owner;

    public TicketStatus status;

    public LocalDateTime lastModified;

    public TopicInfoResponse topic;
}
