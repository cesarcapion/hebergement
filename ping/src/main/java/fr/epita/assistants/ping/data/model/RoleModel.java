package fr.epita.assistants.ping.data.model;

import jakarta.persistence.*;
import jakarta.ws.rs.Consumes;
import lombok.*;

import java.util.List;

@Entity
@Table(name= "roles")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@With
public class RoleModel {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable(
            name= "role_topic_links",
            joinColumns = @JoinColumn(name= "role_id"),
            inverseJoinColumns = @JoinColumn(name= "topic_id")
    )
    private List<TopicModel> topics;

    // FIXME add readonly field that will be set to true for user and admin role
    @Column(name= "read_only")
    private boolean readOnly;
}
