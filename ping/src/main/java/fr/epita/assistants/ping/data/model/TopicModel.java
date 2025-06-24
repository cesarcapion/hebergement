package fr.epita.assistants.ping.data.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Entity
@Table(name= "topics")
@With
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TopicModel {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "topics", fetch = FetchType.EAGER)
    private List<RoleModel> roles;
}
