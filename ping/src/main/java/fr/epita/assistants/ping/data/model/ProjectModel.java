package fr.epita.assistants.ping.data.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;
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
    public UUID uuid;

//    @Column(name="owner_id")
//    public UUID ownerId;

    public String name;
    public String path;

    @ManyToOne
    @JoinColumn(name="owner_id")
    public UserModel owner;

    @OneToMany(mappedBy = "projectUUID", fetch = FetchType.EAGER)
    public List<ProjectMembersModel> members;
}
