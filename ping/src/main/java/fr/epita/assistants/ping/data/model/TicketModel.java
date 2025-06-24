package fr.epita.assistants.ping.data.model;

import fr.epita.assistants.ping.utils.TicketStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tickets")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@With
public class TicketModel {
    @Id
    @GeneratedValue
    public UUID id;

    public String subject;
    public String path;

    @ManyToOne
    @JoinColumn(name="owner_id")
    public UserModel owner;

    @Column(name= "ticket_status")
    @Enumerated(EnumType.STRING)
    private TicketStatus ticketStatus;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name= "ticket_user_links",
            joinColumns = @JoinColumn(name= "project_id"),
            inverseJoinColumns = @JoinColumn(name= "user_id")
    )

    public List<UserModel> members;

    @ManyToOne
    @JoinColumn(name= "topic_id")
    public TopicModel Topic;
}
