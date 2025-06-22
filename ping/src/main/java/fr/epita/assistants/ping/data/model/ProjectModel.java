package fr.epita.assistants.ping.data.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "projects")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@With
public class ProjectModel {
    @Id
    @GeneratedValue
    public UUID id;

    public String name;
    public String path;

    @ManyToOne
    @JoinColumn(name="owner_id")
    public UserModel owner;

    @OneToMany(mappedBy = "projectUUID", fetch = FetchType.EAGER)
    public List<ProjectMembersModel> members;
}
