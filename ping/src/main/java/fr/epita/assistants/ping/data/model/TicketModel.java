package fr.epita.assistants.ping.data.model;

import jakarta.persistence.*;
import lombok.*;

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

    public String name;
    public String path;

    @ManyToOne
    @JoinColumn(name="owner_id")
    public UserModel owner;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name= "ticket_user_links",
            joinColumns = @JoinColumn(name= "project_id"),
            inverseJoinColumns = @JoinColumn(name= "user_id")
    )
    public List<UserModel> members;
}
