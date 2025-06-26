package fr.epita.assistants.ping.data.model;

import fr.epita.assistants.ping.utils.TicketStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name= "tickets_history")
@With
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TicketHistoryModel {
    /** Model of the tickets history to make statistics
     * ticket_id : the ticket were an action occurred
     * interactedBy: the user who interacted with the ticket
     * contentPath: the path to the content of the answer
     * resourcePath: the path to the file added in the answer if there is one (can be null)
     * interactedOn: date of the interaction
     **/
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name= "ticket_id")
    private TicketModel ticket;

    @ManyToOne
    @JoinColumn(name= "interacted_by")
    private UserModel interactedBy;

    @Column(name= "content_path")
    private String contentPath;

    @Column(name= "resource_path")
    private String resourcePath;

    @Column(name= "ticket_status")
    @Enumerated(EnumType.STRING)
    private TicketStatus ticketStatus;

    @Column(name= "interacted_on")
    private LocalDateTime interactedOn;
}
